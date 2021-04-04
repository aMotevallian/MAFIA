public class Player {
    String name;
    boolean alive=true;
    boolean silent=false;
    boolean wakeUpAtNight=false;
    int numOfVotes=0;
    boolean savedByDoc=false;
    Player lastVote=null;
    public Player(String name ){
        this.name=name;
    }
    public void vote(Player p){
        p.numOfVotes++;
    }
    public boolean isAlive(Player p){
        if (!p.savedByDoc){
            p.alive=false;
            return false;
        }
        return true;
    }
    public void nightDuty(Player p){}
}
