import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Aircraft {
	String iD;
	double[] remUsefulLife=new double[6];
	String state;
	Random rn=new Random();
//	double mu=1+(40-1)*rn.nextDouble();
//	double var=mu/(3+(4.7-3)*rn.nextDouble());
//	double stddev= Math.sqrt(var);
	static double threshold=5.00;						// need to change afterwards //
	static double rulDegraderate=1;					// need to change afterwards //
	int timeOfFailtoMaint;
	int timeOfMaintToIdle;
	int startOfMission;
	int endOfMission;
	
	public Aircraft(String iD, String state) {
		this.iD=iD;
		this.remUsefulLife=iniRemUsefulLife(this.remUsefulLife);
		this.state=initialState(remUsefulLife);
		this.timeOfMaintToIdle=timeOfMaintToIdle;
		this.timeOfFailtoMaint=timeOfFailtoMaint;
		this.startOfMission=startOfMission;
		this.endOfMission=endOfMission;
	}
	
	public Aircraft(String iD, double[] remUsefulLife, String state) {
		this.iD=iD;
		this.remUsefulLife=iniRemUsefulLife(remUsefulLife);
		this.state=initialState(remUsefulLife);
		this.timeOfMaintToIdle=timeOfMaintToIdle;
		this.timeOfFailtoMaint=timeOfFailtoMaint;
		this.startOfMission=startOfMission;
		this.endOfMission=endOfMission;
	}
	
	public String toString () {
		ArrayList<Double> rul = new ArrayList<Double>();
		for(double d: remUsefulLife) {
			rul.add(d);
		}
		String str = "Aircraft ID:"  + iD + ", State:" + state + ", RUL:" + rul;
		return str;
	}
	
	int timeOfRULFail(int start, int end, Aircraft ac) {
		ArrayList<Integer> time=new ArrayList<Integer>();
		int timeOfRulFail=0;
		if(ac.state=="Active") {
//			double minRul=Collections.min(ac.remUsefulLife);
			for(int i=0;i<6;i++) {
				time.add(start+(int)(ac.remUsefulLife[i]-threshold));
			}
			timeOfRulFail=Collections.min(time);
		}
		return timeOfRulFail;
	}
	
	int timeOfSudFail(int start, int end, Aircraft ac) {
		int time;
		String state="Active";
		time=start+rn.nextInt(end-start+1);
		//System.out.println("time: "+ time +" Aircraft: "+ ac.iD );
		if(ac.suddenFailure()>=0.9) {
			//ac.remUsefulLife=ac.remUsefulLife(ac, start, end);
//			for(int r=0;r<6; ++r) {
//				ac.remUsefulLife[r]=0.0;
//			}// sudden failure hole 0 kore dichhilo... pore dorkar hole uncomment korbo
			start=time+1;
			return time;
			
		}
		else return 0;
	}
	
	int timeOfFailToMaint(int start, int end, Aircraft ac) {
		int time;
		ac.state="Failed";
		time=start+rn.nextInt(end-start+1);
		//System.out.println("time: "+ time +" Aircraft: "+ ac.iD );
		if(ac.failedToMaint()>=0.9) {
			//ac.remUsefulLife=ac.remUsefulLife(ac.remUsefulLife,state);
			timeOfFailtoMaint=time+1;
			return time;
		}
		else return 0;
	}

	int timeOfMaintToIdle(int start, Aircraft ac) {
		int time;
		String state="Maintenance";
		time=start+rn.nextInt(10); //need to change the range later)
		//System.out.println("time: "+ time +" Aircraft: "+ ac.iD );
		if(ac.failedToMaint()>=0.9) {
			ac.state="Idle";
//			ac.remUsefulLife=ac.remUsefulLife(ac);
			timeOfMaintToIdle=time+1;
			return time;
		}
		else return 0;
	}
	
	double[] iniRemUsefulLife(double[] remUsefulLife) {
		for(int i =0; i<remUsefulLife.length; ++i) {
//			remUsefulLife[i]=(mu+stddev*rn.nextGaussian());
			remUsefulLife[i]=18;//need to change back
		}
		return remUsefulLife;
	}
	
	double suddenFailure() {
		double chance=Math.random();
		return chance;
	}
	
	double checkMaintDone() {
		double chance=Math.random();
		return chance;
		
	}
	
	double[] remUsefulLifeOld(double[] remUsefulLife,String state, Aircraft ac) {
		String currentstate=state;
		if(currentstate=="Active") {
//			double chance=suddenFailure();
			for(int i =0; i<remUsefulLife.length; ++i) {
				remUsefulLife[i]=remUsefulLife[i]-rulDegraderate;
				if(remUsefulLife[i]<=threshold) {
//					remUsefulLife[i]=0.0;
					ac.state="Failed";
				}
//				else if(chance>=0.9) {
//					Arrays.fill(remUsefulLife,new Double(0.0));
//					break;
//				}
			}
		}
//		else if(currentstate=="Idle") {
//			remUsefulLife=remUsefulLife;
//		}
//		else if(currentstate=="Failed" ||currentstate=="inMaint" ) {
//			Arrays.fill(remUsefulLife, 0);
//		}
		else if(currentstate=="inMaint") {
			if(checkMaintDone()>=0.9) {
				currentstate="Idle";
//				mu=1+(40-1)*rn.nextDouble();
//				var=mu/(3+(4.7-3)*rn.nextDouble());
//				stddev= Math.sqrt(var);
				for(int i =0; i<remUsefulLife.length; ++i) {
					if(remUsefulLife[i]==0.0)
//						remUsefulLife[i]=(mu+stddev*rn.nextGaussian());
						remUsefulLife[i]=16;//need to change back
				}
			}
		}
		return remUsefulLife;
	}
	
	double[] remUsefulLife(Aircraft ac, int start, int stop) {
		if(ac.state=="Active") {
			for(int i =0; i<6; ++i) {
				ac.remUsefulLife[i]=ac.remUsefulLife[i]-rulDegraderate;
//				if(ac.remUsefulLife[i]<=threshold) {
//					ac.state="Failed";
////					ac.remUsefulLife[i]=0.0;
//					break;
//				}
			}
		}
//		else if(ac.state=="Failed" || ac.state=="Idle" ||ac.state=="Maintenance" ) {
//			ac.remUsefulLife=ac.remUsefulLife;
//		}
		return ac.remUsefulLife;
	}
	
	String initialState(double[] remUsefulLife) {
		String initialState="";
		for(double rul:remUsefulLife) {
			if(rul<=threshold) {
				initialState="Failed";
//				Arrays.fill(remUsefulLife,new Double(0.0));
				break;
				
			}
			if (rul>threshold) {
				initialState="Idle";
				break;
			}
		}
		return initialState;
	}
	
	double failedToMaint() {
		double chance=Math.random();
		return chance;
	}
	void timeOfRulFailure(Aircraft ac) {
		for(int i=0; i<6;++i) {
			if((ac.endOfMission-ac.startOfMission)*rulDegraderate>=(ac.remUsefulLife[i]-threshold)) {
				int timeOfRulFailure=(int)((ac.remUsefulLife[i]-threshold)/rulDegraderate)+ac.startOfMission;
				ac.endOfMission=timeOfRulFailure;
			}
		}
		
	}
}
