import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;

public class GUI extends JFrame {
    Container con;      // 컨테이너
    JPanel panel1, panel2, panel3;      // 좌측패널(panel1,3), 우측패널(panel2)
    JButton add, delete, start;         // +, -, start 버튼
    JLabel timeQuantum, bt, at;         // 자원 사용 제한 시간, 실행시간, 대기시간
    JTextField btInput, atInput, rrInput;      // 실행시간, 대기시간, 자원 사용 제한 시간을 받기위한 텍스트필드
    JComboBox schedulingInfo;           // 스케줄링 선택하는 콤보박스
    JTable table;                       // 값 출력을 위한 테이블
    JScrollPane scrollPane;             // 테이블 스크롤

    String[][] valueProcessInfo2 = new String[50][6];                   // 테이블 내에 들어가는 데이터
    String[] columnNames2 = {"P.No", "BT", "AT", "WT", "TT", "NTT"};    // 테이블의 열이름

    int cnt;            // 프로세스 개수
    int timeQ;          // 자원 사용 제한 시간
    int totalTime = 0;  // 전체 실행시간
    public static Process process_data[] = new Process[50];     // 프로세서를 저장하는 배열. 출력 상황에 의해 50개로 제한

    public GUI() {
        setTitle("Scheduling");
        setSize(1000, 650); // 총 화면 사이즈 조정(두 패널은 알아서 사이즈 조정 됨)
        setLocationRelativeTo(null);        // 레이아웃에 따라서 배치되는 구역에 그래픽을 중심부터 배치해줌
        setResizable(false);                // 사이즈 조정 불가
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        con = getContentPane();
        con.setLayout(new GridLayout(1, 2));        // 좌, 우를 나누기 위한 배치관리자

        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        // 차트 출력을 위한 테두리와 색 설정
        panel3.setBorder(new TitledBorder(new LineBorder(Color.black), "Scheduler"));
        panel3.setBackground(Color.white);

        panel1.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 0));    // 내부 공백
        panel1.setLayout(new BorderLayout());
        panel2.setLayout(null);
        panel3.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 5)); // 차트 가로 간격 0로 설정
        cnt = 0;
        con.add(panel1);
        panel1.add(panel3);     // panel1 안에 차트 출력을 위한 panel3 추가
        con.add(panel2);

        // 스타트 버튼 기본 설정
        start = new JButton("start");
        start.setBackground(new Color(217, 217, 217));
        start.setForeground(Color.BLACK);
        start.setFont(new Font("바탕", Font.BOLD, 14));
        start.setBounds(345, 60, 90, 30);
        // 스타트 버튼 이벤트
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (cnt != 0) {     // 입력받은 프로세스가 있을때만 실행
                    panel3.removeAll();     // 차트 초기화
                    boolean canStart = true;
                    if (schedulingInfo.getSelectedItem().toString().equals("RR") || schedulingInfo.getSelectedItem().toString().equals("SPN_RR")) {
                        canStart = getRR();        // RR이나 우리만의 알고리즘일 경우 time quantum 입력받는 메소드 호출
                    }
                    if (canStart) {
                        // 프로세스의 개수 입력
                        if (schedulingInfo.getSelectedItem() == "SPN_RR") Schedulings.num_of_processes = cnt;
                        else Schedulings.num_of_processes = cnt;
                        // 프로세스 배열에 값 입력
                        for (int i = 0; i < Schedulings.num_of_processes; i++) {
                            process_data[i] = new Process(Integer.parseInt(valueProcessInfo2[i][1]), Integer.parseInt(valueProcessInfo2[i][2]));
                        }
                        // 해당 스케줄러 메소드 호출
                        if (schedulingInfo.getSelectedItem() == "FCFS") {
                            Schedulings.FCFS_run(process_data);
                            setAllData();   // 계산된 값 테이블에 저장하는 메소드 호출
                        } else if (schedulingInfo.getSelectedItem() == "RR") {
                            Schedulings.RR_run(process_data, timeQ);
                            setAllData();
                        } else if (schedulingInfo.getSelectedItem() == "SPN") {
                            Schedulings.SPN_run(process_data);
                            setAllData();
                        } else if (schedulingInfo.getSelectedItem() == "SRTN") {
                            Schedulings.SRTN_run(process_data);
                            setAllData();
                        } else if (schedulingInfo.getSelectedItem() == "HRRN") {
                            Schedulings.HRRN_run(process_data);
                            setAllData();
                        } else if (schedulingInfo.getSelectedItem() == "SPN_RR") {
                            Schedulings.SPN_RR_run(process_data, timeQ);
                            setAllData();
                        }
                        printButton();      // 차트 출력하는 메소드 호출
                    }
                }
            }
        });

        // + 버튼 기본 설정
        add = new JButton("+");
        add.setBackground(new Color(217, 217, 217));
        add.setForeground(Color.BLACK);
        add.setFont(new Font("바탕", Font.BOLD, 14));
        add.setBounds(335, 538, 45, 30);
        // + 버튼 이벤트
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (cnt < 50) {     // 프로세스 개수가 50개 보다 적을때만 입력
                    setData();      // 테이블에 저장하는 메소드 호출
                    table.repaint();    // 테이블 재작성
                    btInput.setText("");
                    atInput.setText("");
                } else {    // 프로세스 개수가 50개보다 많으면 오류문 출력
                    JOptionPane.showMessageDialog(null, "프로세스 개수가 너무 많습니다.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // - 버튼 기본 설정
        delete = new JButton("-");
        delete.setBackground(new Color(217, 217, 217));
        delete.setForeground(Color.BLACK);
        delete.setFont(new Font("바탕", Font.BOLD, 14));
        delete.setBounds(380, 538, 45, 30);
        // - 버튼 이벤트
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteData();       // 테이블에 저장된 값 지우는 메소드 호출
                table.updateUI();
            }
        });

        // 스케줄링 선택하는 콤보박스 기본 설정
        schedulingInfo = new JComboBox();
        schedulingInfo.setModel(new DefaultComboBoxModel(new String[]{"FCFS", "RR", "SPN", "SRTN", "HRRN", "SPN_RR"}));
        schedulingInfo.setBounds(95, 60, 100, 30);
        // RR이나 우리만의 알고리즘일 때의 이벤트
        schedulingInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                // time quantum 입력 칸의 출력 여부 결정
                if(cb.getSelectedItem().toString().equals("RR") || cb.getSelectedItem().toString().equals("SPN_RR")) {
                    rrInput.setVisible(true);
                    timeQuantum.setVisible(true);
                }
                else {
                    rrInput.setVisible(false);
                    timeQuantum.setVisible(false);
                    rrInput.setText("");
                }

            }
        });

        // 실행 시간 입력 라벨
        bt = new JLabel();
        bt.setBounds(113, 530, 100, 20);
        bt.setText("Burst Time");
        bt.setFont(new Font("바탕", Font.BOLD, 12));

        // 대기 시간 입력 라벨
        at = new JLabel();
        at.setBounds(220, 530, 100, 20);
        at.setText("Arrival Time");
        at.setFont(new Font("바탕", Font.BOLD, 12));

        // time quantum 입력 라벨
        timeQuantum = new JLabel();
        timeQuantum.setText("RR");
        timeQuantum.setFont(new Font("바탕", Font.BOLD, 14));
        timeQuantum.setBounds(232, 60, 50, 30);
        timeQuantum.setVisible(false);      // 보이지 않게 설정

        // 실행시간 입력받는 텍스트필드
        btInput = new JTextField();
        btInput.setBounds(115, 553, 70, 20);
        btInput.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char cnt = e.getKeyChar();// 키보드 입력 받기
                // 숫자 외의 값 입력시 오류문 출력
                if (!((cnt >= '0') && (cnt <= '9') || (cnt == KeyEvent.VK_BACK_SPACE) || (cnt == KeyEvent.VK_DELETE))) {
                    JOptionPane.showMessageDialog(null, "정상적인 입력이 아닙니다.", "Error", JOptionPane.ERROR_MESSAGE);
                    e.consume();    // 오류제거
                }
            }
        });

        // 대기 시간 입력받는 텍스트필드
        atInput = new JTextField();
        atInput.setBounds(230, 553, 70, 20);
        atInput.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char cnt = e.getKeyChar();
                // 숫자 외의 값 입력시 오류문 출력
                if (!((cnt >= '0') && (cnt <= '9') || (cnt == KeyEvent.VK_BACK_SPACE) || (cnt == KeyEvent.VK_DELETE))) {
                    JOptionPane.showMessageDialog(null, "정상적인 입력이 아닙니다.", "Error", JOptionPane.ERROR_MESSAGE);
                    e.consume();
                }
            }
        });

        // time quantum 입력받는 텍스트필드
        rrInput = new JTextField();
        rrInput.setBounds(260, 60, 50, 30);
        rrInput.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char cnt = e.getKeyChar();
                // 숫자 외의 값 입력시 오류문 출력
                if (!((cnt >= '0') && (cnt <= '9') || (cnt == KeyEvent.VK_BACK_SPACE) || (cnt == KeyEvent.VK_DELETE))) {
                    JOptionPane.showMessageDialog(null, "정상적인 입력이 아닙니다.", "Error", JOptionPane.ERROR_MESSAGE);
                    e.consume();
                }
            }
        });
        rrInput.setVisible(false);      // 보이지 않도록 기본 설정

        // 프로세스 숫자, 실행시간, 대기시간, 응답시간, 반환시간, Normalized TT를 출력하는 테이블
        table = new JTable(valueProcessInfo2, columnNames2);
        table.setEnabled(false);
        table.setRowHeight(25);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(90, 120, 350, 400);

        // 오른쪽 패털(panel2)에 테이블, 버튼, 라벨, 텍스트필드 삽입
        panel2.add(add);
        panel2.add(bt);
        panel2.add(at);
        panel2.add(timeQuantum);
        panel2.add(btInput);
        panel2.add(atInput);
        panel2.add(rrInput);
        panel2.add(scrollPane);
        panel2.add(schedulingInfo);
        panel2.add(delete);
        panel2.add(start);
        setVisible(true);
    }

    // +를 눌렀을 때 값을 추가하는 메소드
    public void setData() {
        String str1 = Integer.toString(cnt + 1);
        String str2 = btInput.getText();
        String str3 = atInput.getText();

        if (str2.isEmpty() || str3.isEmpty()) { // 값이 없으면
            JOptionPane.showMessageDialog(null, "값을 입력하지 않으셨습니다.", "공백에러", JOptionPane.ERROR_MESSAGE);
        } else {
            valueProcessInfo2[cnt][0] = str1;   // 프로세스 넘버
            valueProcessInfo2[cnt][1] = str2;   // BT
            valueProcessInfo2[cnt][2] = str3;   // AT
            totalTime += Integer.parseInt(str2);    // 전체 실행 시간 증가
            cnt++;                              // 프로세스 개수 증가
        }
    }

    // time quantum 입력받는 메소드
    public boolean getRR() {
        // 숫자 외의 값 입력시 오류문 출력
        if(rrInput.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "값을 입력하지 않으셨습니다.", "공백에러", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // time quantum이 0일때 오류문 출력
        else if(rrInput.getText().equals("0")) {
            JOptionPane.showMessageDialog(null, "time quantum이 0입니다.", "공백에러", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // time quantum 입력받고 텍스트필드 초기화
        else {
            timeQ = Integer.parseInt(rrInput.getText());
            rrInput.setText("");
            return true;
        }
    }

    // -눌렀을 때 마지막 값을 하나 지우는 메소드
    public void deleteData() {
        if (valueProcessInfo2[0][0] == null) { // 테이블에 값이 없으면
            JOptionPane.showMessageDialog(null, "지울 값이 없습니다.", "공백에러", JOptionPane.ERROR_MESSAGE);
        } else {    // 지울 값이 있으면
            cnt--;  // 프로세스 수 감소
            totalTime -= Integer.parseInt(valueProcessInfo2[cnt][2]);   // 전체 실행 시간 감소
            // 테이블 데이터의 한 행을 삭제
            for (int i = 0; i < 6; i++) {
                valueProcessInfo2[cnt][i] = null;
            }
        }
    }

    // 스케줄러 호출 후 테이블에 계산된 값을 저장하는 메소드
    public void setAllData() {
        for (int i = 0; i < (Schedulings.num_of_processes); i++) {
            valueProcessInfo2[i][0] = Integer.toString(i+1);
            valueProcessInfo2[i][1] = Integer.toString(process_data[i].burst_time);
            valueProcessInfo2[i][2] = Integer.toString(process_data[i].arrival_time);
            valueProcessInfo2[i][3] = Integer.toString(process_data[i].waiting_time);
            valueProcessInfo2[i][4] = Integer.toString(process_data[i].turnaround_time);
            valueProcessInfo2[i][5] = Double.toString(process_data[i].normalized_tt);
            table.updateUI();
        }
    }

    // 왼쪽 패널(panel3)에 차트를 출력하는 메소드
    // 배치관리자의 효율성과 여러 기능을 추가할 경우를 위해 버튼으로 구현
    public void printButton() {
        String[] result = Schedulings.result_s.split(",");      // 스케줄러에서 계산된 결과 순서 저장
        // 프로세스마다 버튼 색 결정
        TreeSet<String> tSet = new TreeSet<String>();   // 중복제거
          for (String s : result) {
              tSet.add(s);
          }
        String[] not_duplicated_string_array = tSet.toArray(new String[0]);
        Random rand = new Random();
        Color[] colors = new Color[not_duplicated_string_array.length];     // 색을 닮을 배열 선언
        // 프로세스 개수많큼 색 결정
        for (int i = 0; i < not_duplicated_string_array.length; i++) {
            colors[i] = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        }

        Stack<JButton> stack = new Stack<JButton>();    // 버튼을 저장할 스택 선언
        // 버튼 생성
        for (int i = result.length-1; i >= 0; i--) {
            JButton button = new JButton();
            button.setEnabled(false);       // 선택 불가
            button.setPreferredSize(new Dimension((panel3.getSize().width-25)/8, 30));  // 크기 설정
            if (result[i].equals("x")) {    // 값이 x면 검은색 X버튼 생성
                button.setText("X");
                button.setBackground(Color.black);
            } else {                        // "P + 프로세스 번호"이름과 지정된 색으로 버튼 생성
                button.setText("P" + result[i]);
                button.setBackground(colors[Integer.parseInt(result[i]) - 1]);
            }
            stack.push(button);     // 스택에 버튼 추가
        };
        // 차례대로 출력하기 위한 타이머
        Timer timer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent timerEvent) {
                if(!stack.isEmpty()) {          // 스택에 값이 있으면
                    panel3.add(stack.pop());    // 버튼 출력
                    panel3.updateUI();
                }
            }
        });
        // 스택에 값이 있으면 타이머를 시작하고 스택이 비면 멈춘다.
        if (!stack.isEmpty()) timer.start();
        else timer.stop();
    }
}