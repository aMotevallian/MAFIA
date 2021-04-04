import java.util.Scanner;

/**
 * github repository link: "https://github.com/aMotevallian/mafiaTextBased.git"
 */
public class Main {
    static Scanner input=new Scanner(System.in);
    static int numOfDay=1;
    static int numOfNight=1;
    static String dayStatus="";

    public static void main(String[] args) {

        int numOfPlayers=0;
        String nameOfPlayers="";
        Player[] players=null;
        int assigned=0;
        boolean gameStillGoing=true , isGameCreated=false , isGameStarted=false;
        while (gameStillGoing){
            String command=input.next();
            switch (command){
                case "create_game":
                    if (!isGameCreated){
                        isGameCreated=true;
                        nameOfPlayers=input.nextLine();
                        String[] splitNames=nameOfPlayers.split(" ");
                        numOfPlayers=splitNames.length-1;
                        players=new Player[numOfPlayers];
                    }
                    else
                        System.out.println("game has created before");
                    break;
                case "assign_role":
                    if (!isGameCreated){
                        System.out.println("no game created");
                        break;
                    }
                    String name=input.next();
                    if (!nameOfPlayers.contains(name)) {
                        System.out.println("user not found");
                        break;
                    }
                    String role=input.next();
                    switch (role){
                        case "Joker": players[assigned++]= new Joker(name); break;
                        case "villager": players[assigned++]= new Villager(name); break;
                        case "detective": players[assigned++]= new Detective(name);break;
                        case "doctor": players[assigned++]= new Doctor(name); break;
                        case "bulletproof": players[assigned++]= new BulletProof(name);break;
                        case "mafia": players[assigned++]= new Mafia(name); break;
                        case "godfather": players[assigned++]= new GodFather(name);break;
                        case "silencer": players[assigned++]= new Silencer(name);break;
                        default:
                            System.out.println("role not found");break;
                    }
                    break;
                case "start_game":
                    if (!isGameCreated){
                        System.out.println("no game created");
                        break;
                    }
                    if (assigned!=numOfPlayers){
                        System.out.println("one or more player do not have a role");
                        break;
                    }
                    isGameStarted=true;
                    for (Player p :players){
                        System.out.println(p.name+" :"+p.getClass().getName());
                    }
                    System.out.println("Ready? Set! Go.");
                    while (isGameStarted){
                        System.out.println(dayStatus);
                        dayStatus="";
                        dayTime(players);
                        players=getAlive(players);
                        if (mafiaCount(players)>=villagerCount(players)){
                            System.out.println("Mafia won");
                            System.exit(0);
                        }
                        if (mafiaCount(players)==0){
                            System.out.println("Villager won");
                            System.exit(0);
                        }
                        nightTime(players);
                        players=getAlive(players);
                        if (mafiaCount(players)>=villagerCount(players)){
                            System.out.println(dayStatus);
                            System.out.println("Mafia won");
                            System.exit(0);
                        }
                        if (mafiaCount(players)==0){
                            System.out.println(dayStatus);
                            System.out.println("Villager won");
                            System.exit(0);
                        }
                    }
                case "get_game_state":
                    players=getAlive(players);
                    System.out.println("Mafia = "+mafiaCount(players));
                    System.out.println("Villager = "+villagerCount(players));
                    break;
                default:
                    break;
            }
        }

    }
    public static void nightTime(Player[] players){
        System.out.println("Night :"+numOfNight++);
        for (Player p:players){
            if (p.wakeUpAtNight)
                System.out.println(p.name+" :"+p.getClass().getName());
            p.silent=false;
        }
        while (true) {
            String first=input.next();
            if (first.equals("end_night"))
                break;
            if (first.equals("start_game")){
                System.out.println("game has already started");
                continue;
            }
            if (first.equals("get_game_state")){
                System.out.println("Mafia = "+mafiaCount(players));
                System.out.println("Villager = "+villagerCount(players));
                continue;
            }
            String second=input.next();
            Player firstPlayer=findPlayer(first , players);
            Player secondPlayer=findPlayer(second , players);
            if (firstPlayer==null||secondPlayer==null){
                System.out.println("user not found");
                continue;
            }
            if (!firstPlayer.alive || !secondPlayer.alive){
                System.out.println("user is dead");
                continue;
            }
            if (!firstPlayer.wakeUpAtNight){
                System.out.println("user can not wake up during night");
                continue;
            }
            if (firstPlayer instanceof Detective || firstPlayer instanceof Doctor)
                firstPlayer.nightDuty(secondPlayer);
            else if (firstPlayer instanceof Silencer && !((Silencer) firstPlayer).nightDutyDone)
                firstPlayer.nightDuty(secondPlayer);
            else{//mafia is voting
                if (firstPlayer.lastVote != null) {//player has vote before and wants to change
                    firstPlayer.lastVote.numOfVotes--;
                }
                firstPlayer.vote(secondPlayer);
                firstPlayer.lastVote = secondPlayer;
            }
        }
        int maxVote=0 ;
        Player toKill=null ,toKill2=null;
        for (Player p1:players){
            for (Player p2:players){
                if (p2.numOfVotes> p1.numOfVotes && p2.numOfVotes>maxVote){
                    maxVote=p2.numOfVotes;
                    toKill=p2;
                }
            }
        }
        int numOfPlayersWithEqualVote=0;
        for (Player p:players){
            if (!p.equals(toKill)&&p.numOfVotes==maxVote) {
                toKill2 = p;
                numOfPlayersWithEqualVote++;
            }
        }
        dayStatus(numOfPlayersWithEqualVote , toKill , toKill2 , players);
        for (Player p:players)
            p.lastVote=null;
    }
    public static void dayStatus(int numOfPlayersWithEqualVote ,Player toKill ,Player toKill2 , Player[] players){
        if (numOfPlayersWithEqualVote==0){
            dayStatus+="Mafia tried to kill "+toKill.name+"\n";
            if (toKill.savedByDoc){
                dayStatus+=toKill.name+" was saved by doctor\n";
            }
            else if (toKill instanceof BulletProof && !((BulletProof) toKill).diedOnce){
                ((BulletProof) toKill).diedOnce=true;
            }
            else {
                dayStatus+=toKill.name+" was Killed\n";
                toKill.alive=false;
            }
        }
        if (numOfPlayersWithEqualVote==1){
            dayStatus+="Mafia tried to kill "+toKill.name+"\n";
            dayStatus+="Mafia tried to kill "+toKill2.name+"\n";
            if (toKill.savedByDoc){
                dayStatus+=toKill.name+" was saved by doctor\n";
                dayStatus+=toKill2.name+" was killed\n";
                toKill2.alive=false;
            }
            else if (toKill instanceof BulletProof && !((BulletProof) toKill).diedOnce){
                ((BulletProof) toKill).diedOnce=true;
                dayStatus+=toKill2.name+" was killed\n";
                toKill2.alive=false;
            }
            else if (toKill2.savedByDoc){
                dayStatus+=toKill2.name+" was saved by doctor\n";
                dayStatus+=toKill.name+" was killed\n";
                toKill.alive=false;
            }
            else if (toKill2 instanceof BulletProof && !((BulletProof) toKill2).diedOnce){
                ((BulletProof) toKill2).diedOnce=true;
                dayStatus+=toKill.name+" was killed\n";
                toKill.alive=false;
            }
        }
        for (Player p:players){
            if (p.silent)
                dayStatus+="Silenced "+p.name+"\n";
        }
        for (Player p:players){
            p.silent=false;
            p.savedByDoc=false;
            if (p instanceof Detective)
                ((Detective) p).detectiveHasAsked=false;
        }
    }
    public static void dayTime(Player[] players){
        System.out.println("Day :"+numOfDay++);

        boolean stillVoting=true;
        while (stillVoting){
            String nameOfVoter=input.next();

            if (nameOfVoter.equals("end_vote")) {
                stillVoting = false;
                break;
            }
            if (nameOfVoter.equals("start_game")){
                System.out.println("game has already started");
                continue;
            }
            if (nameOfVoter.equals("get_game_state")){
                System.out.println("Mafia = "+mafiaCount(players));
                System.out.println("Villager = "+villagerCount(players));
                continue;
            }
            String nameOfVotee=input.next();
            Player voter=findPlayer(nameOfVoter , players);
            Player votee=findPlayer(nameOfVotee , players);
            if (votee==null||voter==null) {
                System.out.println("user not found");
                continue;
            }
            if (!votee.alive) {
                System.out.println("votee already dead");
                continue;
            }
            if (voter.silent) {
                System.out.println("voter is silenced");
                continue;
            }
            if (voter.lastVote != null) {//player has vote before and wants to change
                voter.lastVote.numOfVotes--;
            }
            voter.vote(votee);
            voter.lastVote = votee;
        }

        int maxVote=0 ;
        Player toKill=null ;
        for (Player p1:players){
            for (Player p2:players){
                if (p2.numOfVotes> p1.numOfVotes && p2.numOfVotes>maxVote){
                    maxVote=p2.numOfVotes;
                    toKill=p2;
                }
            }
        }
        for (Player p:players){
            if (!p.equals(toKill)&&p.numOfVotes==maxVote) {
                System.out.println("nobody died");
                toKill = null;
                break;
            }
        }
        if (toKill!=null) {
            toKill.alive = false;
            if (toKill instanceof Joker) {
                System.out.println("Joker won");
                System.exit(0);
            }
            System.out.println(toKill.name + " died");
        }
        for (Player p:players)
            p.lastVote=null;
    }
    public static int mafiaCount(Player[] players){
        int sum=0;
        for (Player p:players){
            if (p instanceof Mafia)
                sum++;
        }
        return sum;
    }
    public static int villagerCount(Player[] players){
        int sum=0;
        for (Player p:players){
            if (p instanceof Villager)
                sum++;
        }
        return sum;
    }
    public static Player[] getAlive(Player[] players){
        Player[] alivePlayers=null;
        int size=0;
        for (Player p:players){
            if (p.alive)
                size++;
            p.numOfVotes=0;
        }
        alivePlayers=new Player[size];
        int index=0;
        for (Player p:players){
            if (!p.alive)
                continue;
            alivePlayers[index++]=p;
        }
        return alivePlayers;
    }
    public static Player findPlayer(String name , Player[] players){
        Player p=null;
        for (Player player: players)
            if (name.equals(player.name))
                p=player;
        return  p;
    }
}
