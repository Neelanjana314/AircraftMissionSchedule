import java.util.ArrayList;

public class Mission {
	int start;
	int end;
	ArrayList<Aircraft> aircraftsInMission;// = new ArrayList<Aircraft>();
	
	public Mission(int start, int end, ArrayList<Aircraft> aircraftsInMission) {
		this.start=start;
		this.end=end;
		this.aircraftsInMission=aircraftsInMission;
	}
	
	public String toString () {
		String str = "Mission Start:"  + start + ", End: " + end;
		String acIdList = ", AircraftList:[";
		if(aircraftsInMission.size()>0) {
			for(Aircraft ac: aircraftsInMission) {
				acIdList = acIdList+ ac.iD+ ", ";
			}
			str = str +acIdList.substring(0, acIdList.length()-2)+"]";
		}
		else
			str = str +", AircraftList: null";
		
			
		return str;
	}
}
