using System;
using System.Collections.Generic;
using System.IO.Ports;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PAC_24Frame.Uart
{
    public class Serial
    {

        private SerialPort serial;

        public delegate void Receive_Listener(string data);
        public event Receive_Listener ReceiveListener;

        public string[] GetCompoartList()
        {
            return SerialPort.GetPortNames();
        }

        public async Task<bool> Open(string comPort, int baudrate)
        {
            if (comPort.Equals("COM1"))
                return false;

            await Task.Run(() =>
            {
                try
                {
                    if (serial != null && serial.IsOpen)
                        return;
                    
                    serial = new SerialPort();
                    serial.PortName = comPort;
                    serial.BaudRate = baudrate;
                    serial.ReadTimeout = 5000;
                    serial.DtrEnable = true;
                    serial.RtsEnable = true;
                    serial.Open();

                    if (serial.IsOpen)
                    {
                        serial.DataReceived += Serial_DataReceived; ;
                        serial.ErrorReceived += Serial_ErrorReceived; ;
                        serial.Disposed += Serial_Disposed;
                    }
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.StackTrace);
                }
            });

            Console.WriteLine("(Serial) Port({0}), Baudrate({1}) = {2})", comPort, baudrate, serial.IsOpen);
            return serial.IsOpen;
        }


        public bool IsOpen()
        {
            if (serial == null)
                return false;
            return serial.IsOpen;
        }

        public void Close()
        {
            if (serial == null || !serial.IsOpen)
                return;

            serial.Close();
            serial.Dispose();
            serial = null;
        }

        private void Serial_Disposed(object sender, EventArgs e)
        {
            Console.WriteLine("(Serial) Disposed.");
        }

        private void Serial_ErrorReceived(object sender, SerialErrorReceivedEventArgs e)
        {
            Console.WriteLine("(Serial) Receive Error.");
        }

        private void Serial_DataReceived(object sender, SerialDataReceivedEventArgs e)
        {
            string recvData = serial.ReadLine().Trim();
            Console.WriteLine("(Serial) Receive Message > {0}", recvData);
            if (ReceiveListener != null)
                ReceiveListener(recvData);
        }
    }
}
