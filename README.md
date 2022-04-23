# Mosk
[2021] Mosk    
_Update: 2022-04-19_  
## **Index**
+ [About this project](#about-this-project)   
+ [Overview](#overview)   
  + [Goal](#goal)   
  + [Flow](#flow)   
+ [Detail Function](#detail-function)
    + [Server](#server)
        + [Send](#send)
    + [App](#app)    
        + [Background](#background)   
        + [Send](#send)   
        + [Receive](#receive)   
        + [Location](#location)   
+ [Environment](#environment)   
    + [Server](#server-platform)
    + [Client](#client-platform)

## **About this project**   
<img src = "https://user-images.githubusercontent.com/68631435/164252230-ff467d43-b2d5-4142-b1aa-97480f6c52a9.png" width="60%" height="40%">     


+ 프로젝트 이름: Mosk: 코로나19 확진자 동선 알림 앱     
+ 프로젝트 진행 목적:  졸업작품      
+ 프로젝트 진행 기간:  2020년 09월~ 2021년 06월        
+ 프로젝트 참여 인원:  2명    

## **Overview** 
> ### **Goal**
+ (목적) 코로나19 전염병 확산 이후, 어떤 장소에 들어갈 때마다 QR코드 체크인, 출입 명부 확인과 같은 번거로운 절차 없이 서비스를 동작하는 것만으로 동선을 저장하여 확진자와의 감염 여부를 파악하기 위함.
+ (필요성) 확진자 동선의 경우 개인 정보에 해당하여 공개를 꺼려 하지만, 일반인의 경우 코로나19 감염 여부를 확인하고 싶어 함.
+ 정확한 이동 동선을 파악할 수 있어 2차 감염을 신속하게 차단할 수 있음.  
> ### **Flow**
<img src = "https://user-images.githubusercontent.com/68631435/164252936-8370a67c-e0ed-4433-951b-dcaf2bbad9a3.png" width="60%" height="40%">     


## **Detail Function**   
>### **Server**   
> 서버의 주요 기능을 구성하는 코드를 설명함.
#### **Send**
```php
def new_client(client_socket, addr, group):
    while True:
        try:
            recv_data = client_socket.recv(1024) # 데이터 수신 대기

            if not recv_data:
                print('Disconnected by',addr)
                group.remove(client_socket)
                break
            
            data = recv_data.decode()
            print(data)

            for c in group:
                if c is not client_socket:
                    c.sendall(recv_data) # 받은 데이터 다시 클라이언트에게 전송
        except:
            # 클라이언트 소켓 강제 종료 시 (ex : 네트워크 변경)
            print('예외발생')
            print('Disconnected by',addr)
            group.remove(client_socket)
            break
    client_socket.close()
```
+ 새로운 클라이언트와 연결하는 통신 스레드를 생성함.  
+ 수신받은 확진자 동선을 모든 Client에게 전송함.
+ 단, 해당 동선을 보낸 확진자에게는 전송하지 않음.   

> ### **APP**   
> 앱의 주요 기능을 구성하는 코드를 설명함.    

#### **Background**
+ MyService class로 단말기의 위치 정보를 지속적으로 저장하는 class
+ 앱을 종료해도 자신의 위치를 저장하고, 확진자 동선을 수신하여 비교할 수 있도록 포그라운드에서 동작함. 

#### **Send**
#### 파일 위치: Mosk/app/src/main/java/com/example/mosk/MapViewFragment.java 
```java
private void SendingService(){
        if (MyService.serviceIntent!=null){
            if (MyService.networKWriter!=null){
                Cursor cursor = locationDB.rawQuery("SELECT * FROM "+tablename, null);
                while(cursor.moveToNext()){
                    final String pretime = cursor.getString(0);
                    final String curtime = cursor.getString(1);
                    final double Lat = cursor.getDouble(2);
                    final double Long = cursor.getDouble(3);

                    if (curtime != null){
                        data = "Data exist";
                        new Thread(){
                            public void run(){
                                // 동선 저장 중인 위치는 전송 x
                                PrintWriter out = new PrintWriter(MyService.networKWriter, true);
                                data = pretime+"/"+curtime+"/"+Lat+"/"+Long;
                                out.println(data);
                                Log.d(TAG,"Send Data : "+data);
                            }
                        }.start();
                    }
                }

                if (data==""){
                    Toast.makeText(getContext(), "전송 할 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                } else{
                    InfectionChartFragment.state = true;
                    data = "";
                    Toast.makeText(getContext(), "데이터를 전송하였습니다.", Toast.LENGTH_SHORT).show();
                }
            } else{
                Toast.makeText(getContext(), "서버 상태 확인 및 1분 후 재시도하세요.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getContext(), "백그라운드 서비스를 먼저 시작해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
```
+ 양성 판정을 받은 사용자는 확진자 버튼 클릭 시 자신의 동선을 서버로 전송함.   
+ 현재 사용자 앱 DB에 저장된 위치정보 및  시각 정보를 전송함.   

#### **Receive**
#### 파일 위치: Mosk/app/src/main/java/com/example/mosk/MyService.java    
```java
private Thread sThread = new Thread("Socket thread"){
        @Override
        public void run() {
            while (true) {
                try {
                    setSocket(ip, port); // 서버 소켓 생성
                    Log.d(TAG, "Make Socket !");

                    while (true) {
                        recv_data = networkReader.readLine(); // 데이터 수신
                        Log.d(TAG, "Recv Data : "+recv_data);
                        String datalist[] = recv_data.split("/");
                        double infLat = Double.parseDouble(datalist[2]);
                        double infLong = Double.parseDouble(datalist[3]);

                        Cursor cursor = locationDB.rawQuery("SELECT * FROM "+tablename+" WHERE preTime<='"+datalist[1]+"' AND curTime>='"+datalist[0]+"'", null);

                        /*생략*/
                
                        }

                        if (recv_data == null) {
                            networKWriter = null;
                            break;
                        }

                    }
                } catch (IOException | ParseException e) {
                    if (sThread == null){
                        Log.d(TAG, "sThread Exit");
                        break; // 스레드를 종료해도 while문이 작동하는 현상 해결
                    } else{
                        try {
                            Log.d(TAG, "Socket Connection Wait..");
                            networKWriter = null;
                            sleep(60000); // 서버와 연결이 안되면, 주기적으로 서버와 연결을 요청함
                        } catch (InterruptedException interruptedException) {
                            Log.d(TAG, "sThread Error");
                            interruptedException.printStackTrace();
                        }
                    }
                }
            }
        }
    };
```
+ 해당 스레드에서는 소켓이 불안정하게 종료될 시(네트워크 변경 등) 주기적으로 서버에 소켓 연결을 요청함. 
+ 수신받은 확진자 동선을 자신의 동선과 비교하는 함수 생략.   
    + 확진자의 동선과 겹친 시간에 비례하여 확률을 세분화 하여 나타내고, 알림이 다르게 표시함.    

#### **Location**
#### 파일 위치: Mosk/app/src/main/java/com/example/mosk/MyService.java 
```java
 private Thread mThread = new Thread("My thread") {
        @Override
        public void run() {
            while (true){
                try{

                    double distance = 0.0;
                    distance = getDistance(pre_lat, pre_lng, Latitude, Longitude);

                    // DB 데이터 확인(생략)

                    // 현재 저장 위치 cnt 확인
                    Cursor cursor2 = locationDB.rawQuery("SELECT * FROM "+tablename+" WHERE curTime is NULL LIMIT 1", null);
                    cnt = cursor2.getCount();
                    Log.d(TAG, "cnt = "+cnt);

                    if (distance < std_distance && cnt == 0){
                        // 최초 저장 
                        //현재시간 가져오기
                        long now = System.currentTimeMillis();
                        Date mDate = new Date(now);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        preTime = simpleDateFormat.format(mDate);
                        locationDB.execSQL("INSERT INTO "+tablename+"(preTime, Latitude, Longitude) VALUES('"+preTime+"', "+pre_lat+", "+pre_lng+")");

                    } else if (distance > std_distance && distance < 99999 && cnt == 1){
                        locationDB.execSQL("UPDATE "+tablename+" SET"+" curTime = datetime('now', 'localtime')"+" WHERE curTime is NULL");
                        Log.d(TAG, "거리: "+distance+" // 위치저장");
                    } else if((distance > std_distance && cnt == 0) || pre_lat == 0.0){
                        //Moving 
                        Log.d(TAG, "거리: "+distance+" // 이동 중..");
                        preTime = null;
                        pre_lat = Latitude;
                        pre_lng = Longitude;
                    } else{
                        //Staying
                        Log.d(TAG, "거리: "+distance+" // 동선 저장 중..");
                    }

                    Log.d(TAG, "pre_location: "+pre_lat+" "+pre_lng);

                    // (생략) DB 데이터 확인

                    sleep(300000);

                } catch (InterruptedException e){
                    Log.d(TAG, "mThread Error");
                    break;
                }
            }
        }
    };
```
+ 단말기의 동선을 저장하는 스레드
+ 크게 4가지 상태로 구분됨 
  + Moving: 사용자가 계속해서 이동하는 상태
  + Detecting Stay: 사용자가 한 장소에 5분 이상 머무른 상태(해당 위치를 저장할 준비를함)
  + Staying: 장소에 계속 머물러 있는 상태, 이때 지도에서 '동선 저장 중 ..'으로 표현됨.
  + Saving Location: 해당 장소를 벗어나면  정보를 저장함.    
    (pretime) 장소에 머물기 시작했던 시각   
    (curtime) 장소를 벗어난 시각   
    (Latitude) 위도   
    (Longtitute) 경도    


## **Environment** 
> ### Server Platform   
+ Name: RaspberryPi 3B+    
+ OS : Raspbian    
+ IDE: Script    
+ CPU : Quad-core 1.4GHz ARM Cortex-A53 MP4 54-bit 1.4GHz       
+ RAM:1GB LPDDR2    
> ### Client Platform
+ Name: Galaxy S10   
+ Processor: 삼성 엑시노스 9 Series (9820) SoC   
+ Memory: 8GB LPDDR4X SDRAM, 126/512 GB   
+ OS: Android 11   
+ SDK Version: API(30)   
+ IDE: Android Studio Version 4.0.1    
+ Gradle Plugin Version: 4.1.3   
+ Gradle Version: 6.5    
