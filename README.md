      
       
<br/>       
        
Android录音实时回放示例    
====      
       
本例设备：     
小米note3       
MIUI 12.0.1      
Android 9     
<br/>     
          
### 2021.11.23。开启回声消除，可以直接使用手机外放
* 1.设置AudioMananger的模式为MODE_IN_COMMUNICATION
AudioManager audoManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
audoManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
* 2.获取AudioRecord的AudioSessionId()，并以此创建AcousticEchoCanceler
int AUDIO_SESSION_ID = audioRecord.getAudioSessionId();
acousticEchoCanceler = AcousticEchoCanceler.create(AUDIO_SESSION_ID);
* 3.为AudioTrack添加这个AUDIO_SESSION_ID
* 4.开启回声消除
acousticEchoCanceler.setEnabled(true);
<br/>

### 2021.9.1
* 临时保活处理，附件v0.4。保持亮屏，防止在后台被杀。
* 使用时适当调暗屏幕。    
<br/>  
  
### 2021.8.31
* 最近任务设置了下图标。  
<br/>     
     
### 2021.8.30晚      
* 支持蓝牙耳机。     
* 注意使用耳机时调节大音量。       
<br/>       
      
### 2021.8.30中午     
* 现在测试的耳机都是带mic的，录音会默认通过耳机。     
* 注意使用耳机时调节大音量。     
<br/>

🚀 2021.11.23。开启回声消除，没有测试apk。

     
🚀 测试apk：      
<a href="https://gitee.com/vigiles/headsetplayback/raw/master/apk/release/app-release-0.4.apk" target="_blank">附件0.4</a>
   

🚀 测试apk：     
<a href="https://gitee.com/vigiles/headsetplayback/raw/master/apk/release/app-release-0.3.apk" target="_blank">附件0.3</a>

    
🚀 测试apk：          
<a href="https://gitee.com/vigiles/headsetplayback/raw/master/apk/release/app-release-0.2.apk" target="_blank">附件0.2</a>
    
<br/>        
     