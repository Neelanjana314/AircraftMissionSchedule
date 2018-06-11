import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;

public class MissionScheduling {
	
	static int totalMissions=5;
	static Random rn = new Random();
	static int start=16+rn.nextInt(40)+1;
	static int duration=2+rn.nextInt(4)+1;
	static int stop=start+rn.nextInt(4)+1;
	static int timeDelay=0;
	static int gap;
	HashMap st= new HashMap();
	HashMap et= new HashMap();
	
	int totalAircrafts=20;
	static String[] ID = {"A1","A2","A3","A4","A5","A6","A7","A8","A9","A10","A11","A12","A13","A14","A15","A16","A17","A18","A19","A20"};	
	static ArrayList<Aircraft> allAircrafts;
	static ArrayList<Aircraft> idleAircrafts;
	static ArrayList<Aircraft> failedAircrafts=new ArrayList<Aircraft>();
	static ArrayList<Aircraft> failedDurMission=new ArrayList<Aircraft>();
	static ArrayList<Aircraft> activeAircrafts;
	static ArrayList<Aircraft> inMaintAircrafts=new ArrayList<Aircraft>();
	static int aircraftNoInMission;
	static ArrayList<Mission> allMission = new ArrayList<Mission>();
	static ArrayList<Integer> time=new ArrayList<Integer>();
	
	public static void main(String[] args) {
		MissionScheduling ms = new MissionScheduling();
		ms.addAircrafts();
		
		ms.printIdle();
		ms.printFailed();
		
//		System.out.println("in main before exec");
//		ms.printAllMission(allMission);
		for(int k=1; k<=totalMissions; k++ ) {
			System.out.println("--------------------");
			System.out.println("Mission No: "+ k);
			System.out.println("--------------------");
//			System.out.println("Start next before gap: "+ start);
			aircraftNoInMission=8+rn.nextInt(5);
			int begin=start;
			int end =stop;
//			System.out.println("begin: "+ begin+ "end: "+ end);
			ms.gapBwtnMissions(ms,aircraftNoInMission);
//			System.out.println("Start next after gap: "+ start);
			start=start+timeDelay;
			stop=start+duration;
			ms.executeMission(begin, end, ms,aircraftNoInMission);
//			ms.gap=0+rn.nextInt(16)+1;
			
		
//		ms.suddenFailureUpdate(ms,begin,end,activeAircrafts);	
//		System.out.println("in main after exec");
//		ms.printAllMission(allMission);
		ms.printFailedInMission();
		ms.printFailed();
		ms.printMaint();
		ms.printIdle();
		}
	}

	void failureUpdate(MissionScheduling ms, int begin, int end, ArrayList<Aircraft> array) {
		ms.printAllMission(allMission);
		for(int i=activeAircrafts.size()-1; i>=0; i--) {
			Aircraft ac = activeAircrafts.get(i);
			int time=ac.timeOfSudFail(begin, end, ac);
			int time1=ac.timeOfRULFail(begin, end, ac);
			System.out.println(ac.iD +"-Sudden Failure time: " + time);
			System.out.println(ac.iD +"-RUL Failure time: " + time1);
			
			//System.out.println("time: "+ time +" Aircraft: "+ ac.iD );
			
			if((time>=begin && time<=end) && (time1>=begin && time1 <=end)) {
				if(time<=time1) {
					ms.sudFailure(ac, time, end);
					ms.printAllMission(allMission);
					ms.printActive();
				}
				 else if(time1<time) {
					ms.rulFailure(ac, time1, end);
					ms.printAllMission(allMission);
					ms.printActive();
				}
			}
			else if((time>=begin && time<=end) && (time1<begin || time1>end)) {
				ms.sudFailure(ac, time, end);
				ms.printAllMission(allMission);
				ms.printActive();
			}
			else if((time<begin || time>end) && (time1>=begin && time1 <=end)) {
				ms.rulFailure(ac, time1, end);
				ms.printAllMission(allMission);
				ms.printActive();
			}
		}
	}
	
	void rulFailure(Aircraft ac, int time1, int end) {
		System.out.println("RUL Failure");
		System.out.println("time1: "+ time1 +" Aircraft: "+ ac.iD );
		ac.state="Failed";
		ac.endOfMission=time1-1;
		ac.remUsefulLife(ac, ac.startOfMission, ac.endOfMission);
		//ms.time.add(time);
		failedAircrafts.add(ac);
		failedDurMission.add(ac);

		activeAircrafts.remove(ac);
//		ms.addSubMission(time+1, end, activeAircrafts);
		int add=0;
		for(int i=idleAircrafts.size()-1; i>=0 ;--i) {
			Aircraft a= idleAircrafts.get(i);//newly added
			for(int j=0; j<6; j++) {
				if(a.remUsefulLife[j]>=(end-ac.endOfMission)) {
					add=1+add;
				}
			}
			if(add==6) {
				activeAircrafts.add(a);//newly added
				a.state="Active";
				idleAircrafts.remove(a);//newly added
				a.startOfMission=time1;//newly added
				a.endOfMission=end;//newly added
			}
			break;
		}
	}
	
	void sudFailure(Aircraft ac, int time,int end) {
		System.out.println("Sudden Failure");
		System.out.println("time: "+ time +" Aircraft: "+ ac.iD );
		ac.state="Failed";
		ac.endOfMission=time;
		//ms.time.add(time);
		failedAircrafts.add(ac);
		failedDurMission.add(ac);
	
		activeAircrafts.remove(ac);
//		ms.addSubMission(time+1, end, activeAircrafts);
		Aircraft a= idleAircrafts.get(idleAircrafts.size()-1);//newly added
		activeAircrafts.add(a);//newly added
		a.state="Active";
		idleAircrafts.remove(a);//newly added
		a.startOfMission=time;//newly added
		a.endOfMission=end;//newly added
//		ms.printAllMission(allMission);
//		ms.printActive();
	}
	
	void executeMission(int start, int end, MissionScheduling ms, int x) {
		
		ms.failedToMaintTransfer(start,end);// start 0 chhilo age
		ms.maintToIdleTransfer();
		
		ms.addMission(start,end,x);
		ms.printActive();
		
//		ms.suddenFailureUpdate(ms,start,end,activeAircrafts);
		ms.failureUpdate(ms,start,end,activeAircrafts);
		
		//failure update e dite hobe ki?
		for(int a=0; a<activeAircrafts.size(); a++) {
			Aircraft ac= activeAircrafts.get(a);
			System.out.println(ac.iD+" start:-"+ac.startOfMission+" end:-"+ac.endOfMission); 
			
			for(int i=0; i<6;i++) {
				ac.remUsefulLife[i]=ac.remUsefulLife[i]-(ac.endOfMission-ac.startOfMission);
			}
		}
		ms.activeToIdleTransfer();
		
		
	}
	
	void activeToIdleTransfer() {
		for(int i=activeAircrafts.size()-1; i>=0; --i) {
			Aircraft ac = activeAircrafts.get(i);
			ac.state="Idle";
			idleAircrafts.add(ac);
		}
	}
	
	void gapBwtnMissions(MissionScheduling ms, int x) {
		gap=0+rn.nextInt(16)+1;
//		System.out.println("Gap between Missions:" +gap);
		start=stop+gap;
//		System.out.println("Start of new Missions:" +start);
		
		while(x>idleAircrafts.size()) {
			ms.failedToMaintTransfer(stop, start);
			ms.maintToIdleTransfer();
			
			if(idleAircrafts.size()>=x) {
				timeDelay=Collections.max(ms.time)-stop;
				break;
			}
		}
		System.out.println("Timedelay:" +timeDelay);
//		System.out.println("Start of new Missions: end of gap:" +start);
	}
	
	void maintToIdleTransfer() {
		for(int i=inMaintAircrafts.size()-1; i>=0; --i) {
			Aircraft a = inMaintAircrafts.get(i);//from failed to inmaint
			int time1 = a.timeOfMaintToIdle(a.timeOfFailtoMaint+3, a);// ??????????????//
			if(time1>a.timeOfFailtoMaint) {
				a.timeOfMaintToIdle=time1;
				a.state="Idle";
//				ms.time.add(time1);
				double mu=1+(40-1)*rn.nextDouble();
				double var=mu/(3+(4.7-3)*rn.nextDouble());
				double stddev= Math.sqrt(var);
				for(int r=0; r<6; ++r) {
					a.remUsefulLife[r]=(mu+stddev*rn.nextGaussian());
				}
				idleAircrafts.add(a);
				inMaintAircrafts.remove(a);
			}
		}
	}
	
	void failedToMaintTransfer(int start,int end) {
		for(int i=failedAircrafts.size()-1; i>=0; --i) {
			Aircraft ac = failedAircrafts.get(i);
			int time=ac.timeOfFailToMaint(start, end, ac);
			if(time>0) {
				ac.state="Maintenance";
				ac.timeOfFailtoMaint=time;
				inMaintAircrafts.add(ac);
				failedAircrafts.remove(ac);
			}
		}
	}
	
	void addAircrafts() {
		allAircrafts = new ArrayList<Aircraft>();
		idleAircrafts = new ArrayList<Aircraft>();
		failedAircrafts = new ArrayList<Aircraft>();
		for(String id:ID) {
			Aircraft a=new Aircraft(id, null);
			allAircrafts.add(a);	
			if(a.state.equals("Failed"))
				failedAircrafts.add(a);
			else
				idleAircrafts.add(a);
		}		
	}

	void addMission(int start, int end,int x) {
			
		Collections.shuffle(idleAircrafts);
				
		activeAircrafts = new ArrayList<Aircraft>();
		int count = 1;
		while(count <= x) {//Idle To Active Transfer
			Aircraft ac = idleAircrafts.remove(idleAircrafts.size()-1);
			ac.startOfMission=start;
			ac.endOfMission=end;
			activeAircrafts.add(ac);
			ac.state="Active";// not working
			++count;
		}
//		System.out.println(activeAircrafts);
		Mission m = new Mission(start, end, activeAircrafts);
		allMission.add(m);
//		System.out.println("in Add mission");
//		printAllMission(allMission);
	}
	
	void printAllMission(ArrayList<Mission> allMission) {
		System.out.println("------------------------------------------------------------");
		System.out.println("allMission: ");
		for(Mission m: allMission) {
			System.out.println(m );
		}
	}
	
	void printFailedInMission() {
		System.out.println("------------------------------------------------------------");
		System.out.println("failedDurMission:");
		for(Aircraft ac: failedDurMission) {
			System.out.println(ac);
		}
	}
	
	void printActive() {
		System.out.println("------------------------------------------------------------");
		System.out.println("activeAircrafts:");
		for(Aircraft ac: activeAircrafts) {
			System.out.println(ac);
		}
	}
	
	void printMaint() {
		System.out.println("------------------------------------------------------------");
		System.out.println("inMaintAircrafts:");
		for(Aircraft ac: inMaintAircrafts) {
			System.out.println(ac);
		}
	}
	
	void printFailed() {
		System.out.println("------------------------------------------------------------");
		System.out.println("failedAircrafts:");
		for(Aircraft ac: failedAircrafts) {
			System.out.println(ac);
		}
	}
	
	void printIdle() {
		System.out.println("------------------------------------------------------------");
		System.out.println("idleAircrafts:");
		for(Aircraft ac: idleAircrafts) {
			System.out.println(ac);
		}
	}
	
}
