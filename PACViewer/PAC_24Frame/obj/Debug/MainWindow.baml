   M S B A M L   `   `   `  ����[  WSystem.Windows.Forms, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089Y@clr-namespace:System.Windows.Forms;assembly=System.Windows.FormsSystem.Windows.Forms  5   	   F BPAC_24Frame, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null)clr-namespace:PAC_24FramePAC_24Frame 5   	   9!clr-namespace:PAC_24Frame.DisplayPAC_24Frame.Display 5   	   A%clr-namespace:PAC_24Frame.EnvironmentPAC_24Frame.Environment 6;      PAC_24Frame.MainWindow   5   	   W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35\ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35^ ZWindowsFormsIntegration, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35F 9http://schemas.microsoft.com/winfx/2006/xaml/presentation     R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     5   	   @d2http://schemas.microsoft.com/expression/blend/2008     5   	   Jmc;http://schemas.openxmlformats.org/markup-compatibility/2006     5   	   Owf@clr-namespace:System.Windows.Forms;assembly=System.Windows.Forms     5   	   +localclr-namespace:PAC_24Frame     5   	   5Display!clr-namespace:PAC_24Frame.Display     5   	   =Environment%clr-namespace:PAC_24Frame.Environment     6;   -   5          � WindowStartupLocation$  CenterScreen=�5
   	    � WindowState$ 	Maximized=�5   	    � WindowStyle$
 None=�5   	    � Topmost .  � Title$ 
MainWindow��5   	    � ShowInTaskbar .  $
��1920��62   $
��1080��6?    � 
ResizeMode$ NoResize=�6M   .��5      � -   5         �Name$ 	MainFrame��5      $
��1920��6   $
��1080��6+   $��Center=�5      $��Center=�6&   ��5   
   �� 5       �� ImageSource$ Res/img_logo.png��6   .+�5   
   #  PAC_24Frame.Display.Displayer  -   5        	  Name$	 Dp_Image��5      $
��Left=�6.   $	��Top=�6I   $
��1080��6a   $
��1920��6o   ,  &PAC_24Frame.Environment.EnvironmentBar  5   
   -   5        
  Name$
 Eb_Air_State��5   %   $
��Left=�6;   $��48��6V   $��Bottom=�6b   $
��1910��6}   $��5,0,0,5q�6�   !  PAC_24Frame.ProgressMessage  5   
   -   5          Name$ 
Pg_Loading��5       $��Center=�64   $��56��6Q   $	��256��6]   $	��Top=�6i   $y�	Collapsed=�6�   7  1System.Windows.Forms.Integration.WindowsFormsHost  5   
   -   5          Name$ Wfh_CCTV��5      $��0��6-   $	��640��6=   $	��360��6I   $
��Left=�6V   $	��Top=�6q   $��3,3,0,0q�6�   $y�	Collapsed=�6�      Child. 5      %   System.Windows.Forms.PictureBox  -   5        Pb_CCTV��5      5      �� 5   
   -   5         ��Name$ Btn_ResetCode��5      $��Reset Device ID��6(   $
��Left=�6B   $��8,8,0,0q�6]   $	��Top=�6n   $	��128��6�   $��32��6�   $y�Hidden=�6�   5       5"      