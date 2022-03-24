import java.util.Deque;
import java.util.LinkedList;

public class Schedulings {
	public static String result_s = new String(); // 프로세스들의 처리 흐름 시각화를 위해 그 흐름을 문자열로 저장하기 위한 변수
	public static int current_time; // 현재 시간의 흐름을 나타내는 변수
	public static int index_of_process; // 바로 처리할 또는 현재 처리 중인 프로세스의 인덱스
	public static int min_arrival_time = 10000; // 준비 큐에 처음 들어온 프로세스의 도착 시간을 가장 작은 도착 시간으로 지정해주기 위해 비교 값으로 적절히 큰 값을 사용
	public static int min_burst_time = 10000; // 준비 큐에 처음 들어온 프로세스의 실행 시간을 가장 작은 실행 시간으로 지정해주기 위해 비교 값으로 적절히 큰 값을 사용
	public static double min_response_ratio; // HRRN에서 사용하는 변수로 → 준비 큐에 있는 프로세스들 중 가장 작은 응답률을 가진 하나의 프로세스를 바로 다음에 처리할 프로세스로 지정해주기 위해 필요한 변수
	public static int num_of_processes; // 전체 프로세스들의 수
	public static int num_of_finished_processes; // 수행이 끝난 프로세스들의 수

	public static int next_process_FCFS(Process processes[]) {
		int ready_queue_front = -1; // 초기화 → 현시각에 처리할 프로세스가 아직 준비 큐에 도착하지 않은 경우 그대로 -1이 반환됨
		
		// 아직 처리가 되지 않은 프로세스들을 대상으로 min_arrival_time을 갱신하는 과정을 통해 현 시각에 준비 큐에 있는 프로세스들 중 가장 먼저 도착한 프로세스를 탐지하여 인덱스 저장
		for (int i = 0; i < num_of_processes; i++)
			if (processes[i].finish == false && processes[i].arrival_time <= current_time
					&& processes[i].arrival_time < min_arrival_time) {
				min_arrival_time = processes[i].arrival_time;
				ready_queue_front = i;
			}

		return ready_queue_front; // FCFS 스케쥴링을 기준으로 다음에 처리할 프로세스의 인덱스를 반환
	}

	public static int next_process_SPN(Process processes[]) {
		int next_process_index = -1; // 초기화 → 현시각에 처리할 프로세스가 아직 준비 큐에 도착하지 않은 경우 그대로 -1이 반환됨
		min_burst_time = 10000; // 다음에 처리할 프로세스를 지정하는데에 있어서 이미 처리된 프로세스의 영향을 받지 않기 위해 min_burst_time 초기화
		
		// 아직 처리가 되지 않은 프로세스들을 대상으로 min_burst_time을 갱신하는 과정을 통해 현 시각에 준비 큐에 있는 프로세스들 중 가장 burst_time이 짧은 프로세스를 탐지하여 인덱스 저장
		for (int i = 0; i < num_of_processes; i++) {
			if (processes[i].finish == false && processes[i].arrival_time <= current_time
					&& processes[i].burst_time < min_burst_time) {
				min_burst_time = processes[i].burst_time;
				next_process_index = i;
			}
		}

		return next_process_index; // SPN 스케쥴링을 기준으로 다음에 처리할 프로세스의 인덱스를 반환
	}
	
    // FCFS
	public static void FCFS_run(Process processes[]) {
		num_of_finished_processes = 0; // 변수 초기화
		result_s = "";
		current_time = 0;

		while (true) {
			if (num_of_finished_processes != num_of_processes) { // 처리가 완료된 프로세스들의 수가 전체 프로세스들의 수보다 적은 경우 → 아직 처리할 프로세스가 남은 경우
				index_of_process = next_process_FCFS(processes); // FCFS 스케쥴링을 기준으로 다음에 처리할 프로세스의 인덱스를 저장
				if (index_of_process == -1) { // 인덱스가 -1일 경우 → 현시각 기준 처리할 프로세스가 아직 준비 큐에 도착하지 않은 경우
					current_time++; // 어떠한 처리도 하지 않고 시간만 흘러가게 함
					result_s += "x,"; // 현 시각 기준 어떠한 프로세스들도 처리가 이루어지지 않는 상태를 시각화하기 위한 "X"를 result_s에 저장
				} else {
					current_time += processes[index_of_process].burst_time; // 처리할 프로세스의 burst_time만큼 현재 시간을 증가
					processes[index_of_process].turnaround_time = current_time // Turn around Time 계산
							- processes[index_of_process].arrival_time;
					processes[index_of_process].waiting_time = processes[index_of_process].turnaround_time // Waiting Time 계산
							- processes[index_of_process].burst_time;
					processes[index_of_process].normalized_tt = (double) processes[index_of_process].turnaround_time // Normalized Turn around Time 계산
							/ processes[index_of_process].burst_time;
					processes[index_of_process].finish = true; // 현재 처리되고 있는 프로세스의 모든 처리과정이 완료되면 해당 프로세스의 finish 필드를 true로 지정
					min_arrival_time = 10000; // 다음에 처리할 프로세스를 지정하는데에 있어서 이미 처리된 프로세스의 영향을 받지 않기 위해 min_arrival_time 초기화
					num_of_finished_processes++; // 처리가 완료된 프로세스들의 수 증가

					for (int i = 0; i < processes[index_of_process].burst_time; i++) { // 해당 처리가 완료된 프로세스의 burst_time과 동일한 개수로
						result_s += Integer.toString(index_of_process + 1) + ","; // 프로세스 number를 result_s에 저장 → 프로세스들의 처리 흐름 시각화 목적
					}
				}
			} else { // FCFS로 모든 프로세스들의 처리가 완료된 경우
				current_time = 0; // 다음에 실행될 스케쥴링 알고리즘의 정상적인 작동을 위해 현재 시간 초기화
				break;
			}
		}
	}
	
	// SPN
	public static void SPN_run(Process processes[]) {
		num_of_finished_processes = 0; // 변수 초기화
		result_s = "";
		current_time = 0;

		while (true) {
			if (num_of_finished_processes != num_of_processes) { // 처리가 완료된 프로세스들의 수가 전체 프로세스들의 수보다 적은 경우 → 아직 처리할 프로세스가 남은 경우
				index_of_process = next_process_SPN(processes); // SPN 스케쥴링을 기준으로 다음에 처리할 프로세스의 인덱스를 저장
				if (index_of_process == -1) { // 인덱스가 -1일 경우 → 현시각 기준 처리할 프로세스가 아직 준비 큐에 도착하지 않은 경우
					current_time++; // 어떠한 처리도 하지 않고 시간만 흘러가게 함
					result_s += "x,"; // 현 시각 기준 어떠한 프로세스들도 처리가 이루어지지 않는 상태를 시각화하기 위한 "X"를 result_s에 저장
				} else {
					current_time += processes[index_of_process].burst_time; // 처리할 프로세스의 burst_time만큼 현재 시간을 증가
					processes[index_of_process].turnaround_time = current_time // Turn around Time 계산
							- processes[index_of_process].arrival_time;
					processes[index_of_process].waiting_time = processes[index_of_process].turnaround_time // Waiting Time 계산
							- processes[index_of_process].burst_time;
					processes[index_of_process].normalized_tt = (double) processes[index_of_process].turnaround_time // Normalized Turn around Time 계산
							/ processes[index_of_process].burst_time;
					processes[index_of_process].finish = true; // 현재 처리되고 있는 프로세스의 모든 처리과정이 완료되면 해당 프로세스의 finish 필드를 true로 지정
					min_burst_time = 10000; // 다음에 처리할 프로세스를 지정하는데에 있어서 이미 처리된 프로세스의 영향을 받지 않기 위해 min_burst_time 초기화
					num_of_finished_processes++; // 처리가 완료된 프로세스들의 수 증가

					for (int i = 0; i < processes[index_of_process].burst_time; i++) { // 해당 처리가 완료된 프로세스의 burst_time과 동일한 개수로
						result_s += Integer.toString(index_of_process + 1) + ","; // 프로세스 number를 result_s에 저장 → 프로세스들의 처리 흐름 시각화 목적
					}
				}
			} else { // SPN으로 모든 프로세스들의 처리가 완료된 경우
				current_time = 0; // 다음에 실행될 스케쥴링 알고리즘의 정상적인 작동을 위해 현재 시간 초기화
				break;
			}
		}
	}
	
	// RR
	public static void RR_run(Process processes[], int time_Q) { // 인자로 처리할 프로세스들의 배열과 Time Quantum을 받아옴
		Deque<Integer> Myqueue = new LinkedList<>(); // Queue 이용
		int[] remain_burst_time = new int[num_of_processes]; // 각 프로세스들의 남은 burst_time을 저장하는 배열 선언
		index_of_process = 0; // 변수 초기화
		num_of_finished_processes = 0;
		result_s = "";
		current_time = 0;

		for (int i = 0; i < num_of_processes; i++) { // 스케쥴링이 실행되기 전 프로세스들의 예측된 burst_time들을 remain_burst_time으로 설정
			remain_burst_time[i] = processes[i].burst_time;
		}

		for (int i = 0; i < num_of_processes; i++) { // 현시각에 준비 큐에 도착한 프로세스들을 Queue에 포함시킴
			if (processes[i].arrival_time == current_time)
				Myqueue.offer(i);
		}

		while (true) {
			if (num_of_finished_processes != num_of_processes) { // 처리가 완료된 프로세스들의 수가 전체 프로세스들의 수보다 적은 경우 → 아직 처리할 프로세스가 남은 경우
				if (Myqueue.isEmpty()) { // 현시각 기준 처리할 프로세스가 아직 준비 큐에 도착하지 않은 경우
					current_time++; // 어떠한 처리도 하지 않고 시간만 흘러가게 함
					result_s += "x,"; // 현 시각 기준 어떠한 프로세스들도 처리가 이루어지지 않는 상태를 시각화하기 위한 "X"를 result_s에 저장!!

					for (int i = 0; i < num_of_processes; i++) { // 갱신된 현재 시간에 준비 큐에 도착한 아직 처리되지 않은 프로세스들이 있을 경우 포함시킴
						if ((processes[i].arrival_time == current_time) && (processes[i].finish == false)) {
							Myqueue.offer(i);
						}
					}
				} else { // 현시각 기준 준비 큐가 비어있지 않은 경우 → 처리할 프로세스가 준비 큐에 존재하는 경우
					index_of_process = Myqueue.poll(); // Queue의 Front에 있는 프로세스의 인덱스를 저장시킴으로써 해당 인덱스의 프로세스를 바로 다음에 처리할 프로세스로 지정
					// isValid의 역할 → 프로세스가 준비 큐에서 빠져서 현재 프로세서에 의해 처리 중일 때 isValid = true, 처리 중이 아닐 때 isValid = false
					boolean isvalid = true;

					for (int i = 0; i < time_Q; i++) { // Time Quantum만큼 시간의 흐름에 따라
						if (isvalid == true) { // 현재 프로세스가 프로세서에 의해 처리 중일 때
							remain_burst_time[index_of_process]--; // 해당 프로세스의 remain_burst_time을 감소시키고
							current_time++; // 현재 시간을 증가시킴
							result_s += Integer.toString(index_of_process + 1) + ","; // 한 개의 프로세스 number를 result_s에 저장 → 프로세스들의 처리 흐름 시각화 목적

							for (int j = 0; j < num_of_processes; j++) { // 갱신된 현재 시간에 준비 큐에 도착한 아직 처리되지 않은 프로세스들이 있을 경우 포함시킴
								if (processes[j].arrival_time == current_time)
									Myqueue.offer(j);
							}

							if (remain_burst_time[index_of_process] == 0) { // 프로세서에 의해 처리 중이던 프로세스의 remain_burst_time이 0인 경우
								processes[index_of_process].finish = true; // 현재 처리되고 있는 프로세스의 모든 처리과정이 완료되었으므로 해당 프로세스의 finish 필드를 true로 지정
								processes[index_of_process].turnaround_time = current_time // Turn around Time 계산
										- processes[index_of_process].arrival_time;
								processes[index_of_process].waiting_time = processes[index_of_process].turnaround_time // Waiting Time 계산
										- processes[index_of_process].burst_time;
								processes[index_of_process].normalized_tt = (double) (processes[index_of_process].turnaround_time) // Normalized Turn around Time 계산
										/ processes[index_of_process].burst_time;
								num_of_finished_processes++; // 처리가 완료된 프로세스들의 수 증가
								// 현재 하나의 프로세스에 대한 처리가 완료되었으므로 다음 새로운 프로세스의 프로세서에 의한 처리가 이루어지기 전까지 isValid 플래그를 false로 지정
								isvalid = false;
							}
						}
					}
					
					if (isvalid) // 프로세스가 프로세서에 의해 선점 처리된 후 아직 해당 프로세스가 완료되지 않았을 때 Queue의 End에 해당 프로세스를 포함시킴
						Myqueue.offer(index_of_process);
				}
			}
			else { // RR로 모든 프로세스들의 처리가 완료된 경우
				current_time = 0; // 다음에 실행될 스케쥴링 알고리즘의 정상적인 작동을 위해 현재 시간 초기화
				num_of_finished_processes = 0;
				break;
			}
		}
	}
	
//SRTN
	public static void SRTN_run(Process processes[]) {//SRTN에 대한 계산을 실행하는 메소드
		int[] remain_burst_time = new int[num_of_processes]; // 프로세서의 Burst Time 시간 저장
		result_s = "";//결과값을 문자열 형태로 저장해 두는 곳(GUI시각화를 위해 참조하는 데이터)
		num_of_finished_processes = 0;//끝난 프로세서의 개수를 0으로 초기화
		current_time = 0;//현재 시간의 시간을 0으로 초기화

		for (int i = 0; i < num_of_processes; i++)
			remain_burst_time[i] = processes[i].burst_time;//모든 프로세서의 남은 실행시간을 일단 프로세서의 실행 시간으로 초기화

		while (num_of_finished_processes < num_of_processes) {//모든 프로세서가 종료될때까지 반복
			index_of_process = next_process_SRTN(remain_burst_time, processes);//next_process_SRTN에서 반환하는 인덱스 값을 현재 프로세스 인덱스에 저장
			if (index_of_process == -1) {//만약 SRTN을 실행할 프로세스가 없다면
				current_time++;//현재 시간을 1초 더하고
				result_s += "x,";//프로세서를 실행하지 않았다는 의미로 'x'를 결과값에 저장
			} else {//만약 SRTN을 실행할 프로세스가 있다면
				remain_burst_time[index_of_process] -= 1;//현재 프로세서가 1초 실행되었으므로 남은 프로세서의 실행시간을 1초 감소
				current_time++;//현재 시간 증가
				result_s += Integer.toString(index_of_process + 1) + ",";//결과값에 프로세서의 넘버를 출력함(인덱스이므로 +1을 해줌)
				if (remain_burst_time[index_of_process] == 0) {//만약 현재 인덱스의 프로세스가 남은 실행시간이 0이 되었다면
					processes[index_of_process].finish = true;//현재 인덱스의 프로세스를 끝났다고 선언
					processes[index_of_process].turnaround_time = current_time//현재 인덱스의 turn_around시간을 계산 
							- processes[index_of_process].arrival_time;
					processes[index_of_process].waiting_time = processes[index_of_process].turnaround_time//현재 인덱스의 waiting시간을 계산
							- processes[index_of_process].burst_time;
					processes[index_of_process].normalized_tt = (double) (processes[index_of_process].turnaround_time//현재 인덱스의 NTT를 계싼
							/ processes[index_of_process].burst_time);
					num_of_finished_processes++;//끝난 프로세스의 개수를 +1 증가시킴
				}
			}
		}
		current_time = 0;//모든 프로세스에 대해 SRTN이 끝나면 다른 스케줄링 알고리즘이 참조할때를 생각하여 다시 0으로 초기화
		min_burst_time = 10000;//마찬가지로 min_burst_time을 10000으로 다시 초기화
		num_of_finished_processes = 0;//마찬가지로 끝난 프로세스의 수를 0으로 초기화
	}

	public static int next_process_SRTN(int remain_burst_time[], Process processes[]) {//가장 남은시간이 짧은 프로세스의 인덱스 값을 반환
		int next_process_index = -1;//반환해야할 프로세스의 값을 -1로 초기화
		min_burst_time = 10000;//가장 짧은 남은시간의 변수를 10000으로 초기화

		for (int i = 0; i < num_of_processes; i++) {//프로세서의 수만큼 for문을 돌려서 가장 남은시간이 짧은 프로세스를 찾는 과정
			if (processes[i].finish == false && processes[i].arrival_time <= current_time
					&& remain_burst_time[i] < min_burst_time) {//끝나지 않은 프로세서 중에서 도착시간이 현재 시간보다 작고 남은 시간이 제일 짧은 프로세서를 찾으면
				min_burst_time = remain_burst_time[i];//가장 남은시간이 짧은 시간을 저장
				next_process_index = i;//가장 남은시간이 짧았던 프로세서 인덱스 값을 저장
			}
		}

		return next_process_index;//가장 남은시간이 짧았던 프로세서 인덱스 값을 반환
	}

	public static int next_process_HRRN(Process processes[]) {//응답률의 값에 따른 프로세스 값을 반환해주는 메소드
		int next_process_index = -1;//HRRN을 통해 어떤 프로세스가 실행되어야하는지 인덱스를 반환해주는 것

		for (int i = 0; i < num_of_processes; i++) {//프로세서의 총 개수 만큼 실행하여 모든 프로세스에 대해 응답률을 계산
			processes[i].responseRatio = Math//각각의 프로세스에 대한 응답률값을 계산하여 저장
					.round((((double) ((current_time - processes[i].arrival_time) + processes[i].burst_time)
							/ (double) processes[i].burst_time) * 100) / 100);
		}

		for (int i = 0; i < num_of_processes; i++) {//모든 프로세스들에 대하여
			if (processes[i].finish == false && processes[i].arrival_time <= current_time
					&& processes[i].responseRatio > min_response_ratio) {//끝나지 않은 프로세스에 대하여 도착시간이 현재시간보다 작고 응답률값이 가장 큰 프로세스에 대해
				min_response_ratio = processes[i].responseRatio;//가장 응답률이 큰 값을 저장
				next_process_index = i;//가장 응답률이 큰 프로세스의 인덱스 값을 저장
			}
		}

		return next_process_index;//응답률이 큰 프로세스의 인덱스 값을 반환해줌

	}
//HRRN
	public static void HRRN_run(Process processes[]) {//HRRN의 계산을 수행하는 알고리즘
		num_of_finished_processes = 0;//끝난 프로세스의 개수를 0으로 초기화
		result_s = "";//결과 값을 넣을 result_s를 초기화(GUI시각화를 위해 참조하는 데이터)
		current_time = 0;//현재시간을 0으로 초기화

		while (true) {//무한 반복
			if (num_of_finished_processes != num_of_processes) {
				index_of_process = next_process_HRRN(processes); // HRRN을 실행할 인덱스 값을 현재 프로세스 인덱스에 저장
				if (index_of_process == -1) {//만약 실행할 프로세서가 없다면
					current_time++;//현재시간 +1
					result_s += "x,";//결과값에 'x'를 추가하여 실행한 프로세서가 없음을 알려줌
				} else {//실행할 프로세서가 있다면
					current_time += processes[index_of_process].burst_time;//현재시간에 현재 프로세스의 실행시간만큼 더하고
					processes[index_of_process].turnaround_time = current_time//turn around시간을 계산
							- processes[index_of_process].arrival_time;
					processes[index_of_process].waiting_time = processes[index_of_process].turnaround_time//waiting시간을 저장
							- processes[index_of_process].burst_time;
					processes[index_of_process].normalized_tt = (double) processes[index_of_process].turnaround_time//NTT시간을 저장
							/ processes[index_of_process].burst_time;
					processes[index_of_process].finish = true;//현재 프로세스를 끝났다고 알려줌
					min_response_ratio = 0.0;//응답률을 다시 0으로 초기화
					num_of_finished_processes++;//끝난 프로세스의 시간을 +1함
					for (int i = 0; i < processes[index_of_process].burst_time; i++) {
						result_s += Integer.toString(index_of_process + 1) + ",";//이제까지 수행한 프로세스의 인덱스 값을 실행시간만큼 결과값에 저장
					}
				}
			} else {//모든 프로세서가 종료되었다면 
				current_time = 0;//현재시간을 0으로 초기화
				break;//while문을 종료함
			}
		}
	}
	
	// 우리만의 알고리즘의 개요 →
	public static void SPN_RR_run(Process processes[], int time_Q) { // 인자로 처리할 프로세스들의 배열과 Time Quantum을 받아옴
		Deque<Integer> Myqueue = new LinkedList<>(); // RR 알고리즘 수행을 위해 Queue 이용
		
		int[] remain_burst_time = new int[num_of_processes]; // 각 프로세스들의 남은 burst_time을 저장하는 배열 선언
		index_of_process = 0; // 변수 초기화
		num_of_finished_processes = 0;
		result_s = "";
		current_time = 0;
		
		for (int i = 0; i < num_of_processes; i++) { // 스케쥴링이 실행되기 전 프로세스들의 예측된 burst_time들을 remain_burst_time으로 설정
			remain_burst_time[i] = processes[i].burst_time;
		}
		
		// 현시각에 처리할 프로세스가 아직 SPN 준비 큐에 도착하지 않은 경우에는 밑에 while문의 if(Myqueue.isEmpty())부에서 current_time을 증가시켜준 이후
		// SPN 기준 바로 다음으로 처리될 프로세스의 인덱스를 RR Queue의 Front에 포함시킴
		while (num_of_finished_processes < num_of_processes) { // 처리가 완료된 프로세스들의 수가 전체 프로세스들의 수보다 적은 경우 → 아직 처리할 프로세스가 남는 동안에
			if (Myqueue.isEmpty()) { // 현시각 기준 처리할 프로세스가 아직 SPN 준비 큐에 도착하지 않아 SPN 기준으로 RR Queue로 가져올 수 없어 RR Queue가 비어있을 경우

				if (next_process_SPN(processes) == -1) {
					result_s += "x,";
					current_time++;
				}
				
				else Myqueue.offer(next_process_SPN(processes)); // SPN 준비 큐에 존재하는 프로세스들 중 실행시간이 가장 짧은 프로세스를 RR Queue의 Front에 포함시킴
			}
			
			else if (Myqueue.size() <= 2) { // SPN 기준 바로 다음에 처리될 프로세스가 두 개이거나 전체 프로세스들 중 마지막 한 개의 프로세스가 남았을 때 그 프로세스에 대한 처리부
				index_of_process = Myqueue.poll(); // RR Queue의 Front에 있는 프로세스의 인덱스를 저장시킴으로써 해당 인덱스의 프로세스를 바로 다음에 처리할 프로세스로 지정
				boolean isvalid = true; // isValid의 역할 → 프로세스가 준비 큐에서 빠져서 현재 프로세서에 의해 처리 중일 때 isValid = true, 처리 중이 아닐 때 isValid = false

				for (int i = 0; i < time_Q; i++) { // Time Quantum만큼 시간의 흐름에 따라
					if (isvalid) { // 현재 어떠한 하나의 프로세스가 프로세서에 의해 선점 처리 중일 경우
						remain_burst_time[index_of_process]--; // 해당 프로세스의 remain_burst_time을 감소시키고
						current_time++; // 현재 시간을 증가시킴
						result_s += Integer.toString(index_of_process + 1) + ","; // 한 개의 프로세스 number를 result_s에 저장 → 프로세스들의 처리 흐름 시각화 목적
					}
					
					if (remain_burst_time[index_of_process] == 0) { // 프로세서에 의해 처리 중이던 프로세스의 remain_burst_time이 0인 경우
						processes[index_of_process].finish = true; // 현재 처리되고 있는 프로세스의 모든 처리과정이 완료되었으므로 해당 프로세스의 finish 필드를 true로 지정
						processes[index_of_process].turnaround_time = current_time // Turn around Time 계산
								- processes[index_of_process].arrival_time;
						processes[index_of_process].waiting_time = processes[index_of_process].turnaround_time // Waiting Time 계산
								- processes[index_of_process].burst_time;
						processes[index_of_process].normalized_tt = (double) (processes[index_of_process].turnaround_time) // Normalized Turn around Time 계산
								/ processes[index_of_process].burst_time;
						num_of_finished_processes++; // 처리가 완료된 프로세스들의 수 증가
						// 현재 하나의 프로세스에 대한 처리가 완료되었으므로 다음 새로운 프로세스의 프로세서에 의한 처리가 이루어지기 전까지 isValid 플래그를 false로 지정
						isvalid = false;
						remain_burst_time[index_of_process] = -1; // 처리가 완료된 해당 프로세스의 remain_burst_time을 -1로 지정
					}
				}
				// 프로세스가 프로세서에 의해 선점 처리된 후 Time Quantum이 지난 후에도 해당 프로세스가 완료되지 않았을 때 RR Queue의 End에 해당 프로세스를 다시 포함
				if (isvalid)
					Myqueue.offer(index_of_process);
				
				if (Myqueue.size() == 1) { // SPN 기준으로 선정되어 RR Queue로 가져왔었던 두 개의 프로세스 중 하나의 프로세스에 대한 처리가 완료된 이후
					index_of_process = Myqueue.poll(); // RR Queue의 Front에 남아있는 프로세스의 인덱스를 저장시킴으로써 해당 인덱스의 프로세스를 바로 다음에 처리할 프로세스로 지정
					// SPN 준비 큐에 존재하는 프로세스들 중 하나의 프로세스를 선정하여 RR Queue로 가져올 때 이미 가져온 프로세스를 다시 한 번 가져오는 경우를 방지하기 위해
					// next_process_SPN을 실행시키기 전 이미 Queue로 가져온 프로세스의 finish 필드를 true로 임시 지정
					processes[index_of_process].finish = true;
					if (next_process_SPN(processes) != -1) // SPN 기준 바로 다음에 처리할 프로세스가 SPN 준비 큐에 존재하는 경우
						Myqueue.offer(next_process_SPN(processes)); // SPN 준비 큐에 존재하는 프로세스들 중 실행시간이 가장 짧은 프로세스를 RR Queue의 Front에 포함시킴
					// 임시 설정해주었던 해당 프로세스의 finish 필드를 false로 다시 지정 → 해당 프로세스는 사실 RR Queue에서 처리를 기다리는 입장이기 때문
					processes[index_of_process].finish = false;
					// 원래 RR Queue에 남아있던 프로세스를 Queue에서 빼고 next_process_SPN으로 결정된 인덱스를 먼저 넣고 그 다음에 원래 있던 것을 넣는 것보다
					// 임시 설정을 하지 않음으로써 원래 RR Queue에 남아있는 하나의 프로세스와 새로 SPN 준비 큐에 도착한 프로세스들을 다 함께 고려하여!!
					// 실시간으로 우선순위에 RR Queue에 들여보내도록 하는 방식은 어떤지 물어보기!!
					// 발생하는 오류 → Time Quantum이 끝나기 전에 프로세스가 처리 완료되는 부분이 없기 때문임!!
					Myqueue.offer(index_of_process);
				}
			}
			// RR Queue에는 다음에 처리할 프로세스가 전체 중에서 마지막 프로세스일 경우를 제외하면
			// 프로세스가 1개만 있을 때 현시각에 SPN 준비 큐에 도착한 프로세스들 중 SPN 기준 가장 우선순위가 높은 하나의 프로세스를 RR Queue에 포함시키는 방향으로 운영됨
			// 하지만 RR Queue에 2개의 프로세스가 존재할 경우 더 이상 SPN 준비 큐에서 프로세스를 가져오지 않고 바로 처리가 이루어지기 때문에 RR Queue에는 세 개 이상의 프로세스가 존재할 수 없음
			// 따라서 RR Queue에 3개 이상의 프로세스가 존재할 경우 알고리즘이 제대로 작동하지 않고 있다는 것을 확인하기 위해 메시지를 출력할 수 있도록 함
			// else System.out.println("오류발생_덱에 3개 이상 입력 상태");!!
		}
		current_time = 0; // 다음에 실행될 스케쥴링 알고리즘의 정상적인 작동을 위해 현재 시간 초기화
	}
}