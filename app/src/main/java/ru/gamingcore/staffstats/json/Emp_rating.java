package ru.gamingcore.staffstats.json;

public class Emp_rating {
    public String exp_emp = "";
    public int size = 0;
    public String[] month = new String[12];
    public String[] knld = new String[12];//знания/умения
    public String[] soc = new String[12];//коммуникабельность
    public String[] resp = new String[12];//ответственность
    public String[] activ = new String[12];//активность
    public String[] innov = new String[12];//инновационность
    public String[] ent = new String[12];//предприимчивость
    public String avr_knld = "";
    public String avr_soc = "";
    public String avr_resp = "";
    public String avr_activ = "";
    public String avr_innov = "";
    public String avr_ent = "";

    public int getSize() {
        return size;
    }

    public double[] getCurrent() {
        return getByMonth(11);
    }

    public String getMonth(int n) {
        return month[n];
    }

    public double[] getByMonth(int m) {
        double[] values = new double[6];
        values[0] = Double.valueOf(knld[m]);
        values[1] = Double.valueOf(soc[m]);
        values[2] = Double.valueOf(resp[m]);
        values[3] = Double.valueOf(activ[m]);
        values[4] = Double.valueOf(innov[m]);
        values[5] = Double.valueOf(ent[m]);
        return values;
    }

    public double[] getAvr() {
        double[] values = new double[6];
        values[0] = Double.valueOf(avr_knld);
        values[1] = Double.valueOf(avr_soc);
        values[2] = Double.valueOf(avr_resp);
        values[3] = Double.valueOf(avr_activ);
        values[4] = Double.valueOf(avr_innov);
        values[5] = Double.valueOf(avr_ent);
        return values;
    }
}