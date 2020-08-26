#include <SoftwareSerial.h>

// Leonardo SoftwareSerial pin : 8 ~ 11

#define VOC_HCHO_PIN        A0
#define DUST_PMS7003_TX     5
#define DUST_PMS7003_RX     11
#define LED_PIN             12
#define BTN_PIN             2
#define FAN_PIN             10
#define FAN_PW_PIN          9
#define PLASMA1_PIN         7
#define PLASMA2_PIN         8

#define GOOD_VOC_PPM  1
#define NORMAL_VOC_PPM  2
#define BAD_VOC_PPM  3

#define GOOD_DUST_PPM  15
#define NORMAL_DUST_PPM  35
#define BAD_DUST_PPM  75

SoftwareSerial dSerial(DUST_PMS7003_RX, DUST_PMS7003_TX);

#define Vc 4.95
#define R0 32.00

const int sensingInterval = 3000;
unsigned long sensingTime = 0;
const int plasmaBlinkInterval = 5000;
unsigned long plasmaBlinkTime = 0;

byte dustCount = 0;
unsigned char dustData[30];
float dustValue;
float vocBuf[5];
int vocBufCnt = -1;
float avgVoc = 0;

bool autoState = false;
int fanSpeed = 0;
int fanMaxSpeed = 3;
int pressureCnt = 0;

void setup() {
  Serial.begin(9600);

  while (!Serial);

  dSerial.begin(9600);

  pinMode(BTN_PIN, INPUT);
  pinMode(FAN_PIN, OUTPUT);
  pinMode(PLASMA1_PIN, OUTPUT);
  pinMode(PLASMA2_PIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);
  pinMode(FAN_PW_PIN, OUTPUT);

  digitalWrite(PLASMA1_PIN, LOW);
  digitalWrite(PLASMA2_PIN, LOW);
  digitalWrite(LED_PIN, LOW);
  digitalWrite(FAN_PW_PIN, LOW);
  analogWrite(FAN_PIN, 0);
}

void btnControl() {
  if (digitalRead(BTN_PIN)) {
    while (digitalRead(BTN_PIN)) {
      pressureCnt++;
      //      Serial.print(F("# Button Pressure - "));
      //      Serial.println(pressureCnt);
      delay(100);
    }

    if (pressureCnt < 12) {
      autoState = false;
      fanSpeed = fanSpeed == fanMaxSpeed ? 0 : fanSpeed + 1;
      setFanSpeed(fanSpeed);
    } else {
      autoState = !autoState;
      //      Serial.print(F("# Auto Mode : "));
      //      Serial.println(autoState);
    }
    digitalWrite(LED_PIN, autoState);
    pressureCnt = 0;
  }
}

void loop() {
  btnControl();

  if (millis() - sensingTime >= sensingInterval) {
    measureVOCSensor();
    measureDustSensor();
    sendAirQuality();

    if (autoState) {
      autoControl();
    }
    sensingTime = millis();
  }

  setPlasmaStatus();

  delay(25);
}

void setPlasmaStatus() {
  if (fanSpeed == 0) {
    digitalWrite(PLASMA1_PIN, false);
    digitalWrite(PLASMA2_PIN, false);
    plasmaBlinkTime = 0;
  } else {
    digitalWrite(PLASMA1_PIN, true);
    if (millis() - plasmaBlinkTime >= plasmaBlinkInterval) {
      digitalWrite(PLASMA2_PIN, !digitalRead(PLASMA2_PIN));
      plasmaBlinkTime = millis();
    }
  }
}

void sendAirQuality() {
  String msg = "$" + String(dustValue) + "," + String(avgVoc) + "#";
  Serial.println(msg);
  delay(1);
}

void setFanSpeed(int speed) {
  Serial.print(F("# Fan Speed : "));
  Serial.println(speed);
  int pwm;
  if (speed == 0) {
    pwm = 0;
  } else if (speed == 1) {
    pwm = 75;
  } else if (speed == 2) {
    pwm = 150;
  } else {
    pwm = 250;
  }
  digitalWrite(FAN_PW_PIN, pwm != 0);
  analogWrite(FAN_PIN, pwm);
}

void autoControl() {
  int autoSpeed;
  if (avgVoc < GOOD_VOC_PPM && dustValue <= GOOD_DUST_PPM) {
    autoSpeed = 0;
  } else if (avgVoc > NORMAL_VOC_PPM || dustValue > NORMAL_DUST_PPM) {
    autoSpeed = 1;
  } else if (avgVoc > BAD_VOC_PPM || dustValue > BAD_DUST_PPM) {
    autoSpeed = 2;
  } else {
    autoSpeed = 3;
  }

  if (autoSpeed != fanSpeed) {
    fanSpeed = autoSpeed;
    setFanSpeed(fanSpeed);
  }
}

float analogFilter(int& cnt, float buf[], float val) {
  float totalVal = val;
  if (cnt == 5) {
    for (int i = 0; i < 5; i++) {
      totalVal += buf[i];
    }
    cnt = 0;
    return totalVal / 5;
  } else if (cnt == -1) {
    buf[++cnt] = val;
    return val;
  } else {
    buf[cnt++] = val;
    return -1;
  }
}

void measureVOCSensor() {
  // http://wiki.seeedstudio.com/Grove-HCHO_Sensor/
  double Rs = (1023.0 / analogRead(VOC_HCHO_PIN)) - 1;
  double ppm = pow(10.0, ((log10(Rs / R0) - 0.0827) / (-0.4807)));
  float avg = analogFilter(vocBufCnt, vocBuf, (float)ppm);
  avgVoc = avg < 0 ? avgVoc : avg;

  //  Serial.print(F("# [VOC] HCHO > "));
  //  delay(1);
  //  Serial.print(Rs);
  //  delay(1);
  //  Serial.print(F(" (Rs), "));
  //  delay(1);
  //  Serial.print(ppm);
  //  delay(1);
  //  Serial.println(F(" (ppm)"));
  //  delay(1);
}

void measureDustSensor() {
  //https://makernambo.com/79
  if (dSerial.available()) {
    for (int i = 0; i < 32; i++) {
      char chrRecv = dSerial.read();
      if (chrRecv == 0x4d) {
        dustCount = 2;
        break;
      }
    }
    if (dustCount == 2)
    {
      dustCount = 0;
      for (int i = 0; i < 30; i++) {
        dustData[i] = dSerial.read();
      }

      if ( (unsigned int) dustData[27] == 0 ) {
        int PM01  = (dustData[8] << 8) + dustData[9];
        int PM25  = (dustData[10] << 8) + dustData[11];
        int PM10  = (dustData[12] << 8) + dustData[13];
        //        Serial.print(F("# [DUST] PMS7003 > "));
        //        delay(1);
        //        Serial.print(PM01);
        //        delay(1);
        //        Serial.print("(PM1.0), ");
        //        delay(1);
        //        Serial.print(PM25);
        //        delay(1);
        //        Serial.print("(PM2.5), ");
        //        delay(1);
        //        Serial.print(PM10);
        //        delay(1);
        //        Serial.println("(PM10)");
        //        delay(1);
        if (PM25 > 0 && PM25 < 1000)
          dustValue = PM25;
      }
    }
  }
}
