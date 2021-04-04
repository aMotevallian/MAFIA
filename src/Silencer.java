public class Silencer extends Mafia{
    boolean nightDutyDone=false;
    public Silencer(String name){
        super(name);
        this.wakeUpAtNight=true;
    }
    public void silent(Player p){
        p.silent=true;
    }

    @Override
    public void nightDuty(Player p) {
        silent(p);
        nightDutyDone=true;
    }
}
