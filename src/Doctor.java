public class Doctor extends Villager{
    public Doctor(String name){
        super(name);
        this.wakeUpAtNight=true;
    }
    public void save(Player p){
        p.savedByDoc=true;
    }

    @Override
    public void nightDuty(Player p) {
        save(p);
    }
}
