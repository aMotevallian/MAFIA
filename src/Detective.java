public class Detective extends Villager{
    boolean detectiveHasAsked=false;
    public Detective(String name){
        super(name);
        this.wakeUpAtNight=true;
    }
    public void isMafia(Player p){
        if (p instanceof GodFather){
            System.out.println("NO");
            return;
        }
        if (p instanceof Mafia) {
            System.out.println("Yes");
        } else {
            System.out.println("No");
        }
    }

    @Override
    public void nightDuty(Player p) {
        if (p==null) {
            System.out.println("user not found");
            return;
        }
        if (!p.alive){
            System.out.println("suspect is dead");
            return;
        }
        if (!detectiveHasAsked){
            detectiveHasAsked=true;
            isMafia(p);
        }
        else
            System.out.println("detective has already asked");
    }
}
