public class Process {
	public int arrival_time;
	public int burst_time;
	public int waiting_time;     // waiting_time = turnaround_time - burst_time
	public int turnaround_time;
	public double normalized_tt; // normalized_tt(normalized_tt = turnaround_time / burst_time)
	public boolean finish;       // if finish == true → 실행 X if finish == false → 실행 O
	public float responseRatio;
	
	public Process() {
		this.arrival_time = 0;
		this.burst_time = 0;
		this.finish = false;
	}

	public Process(int bt, int at) {
		this.arrival_time = at;
		this.burst_time = bt;
		this.finish = false;
	}
	
	public String print() {
		return " | " + String.format("%2d ", arrival_time)   + " | " + String.format("%2d ", burst_time)
		     + " | " + String.format("%2d ", waiting_time)   + " | " + String.format("%2d ", turnaround_time)
		     + " | " + String.format("%2.1f", normalized_tt) + " | ";
	}
}