   M S B A M L   `   `   `  ����[  WSystem.Windows.Forms, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089Y@clr-namespace:System.Windows.Forms;assembly=System.Windows.FormsSystem.Windows.Forms  5   	   E APAC_7Frame, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null'clr-namespace:PAC_7Frame
PAC_7Frame 5   	   7 clr-namespace:PAC_7Frame.DisplayPAC_7Frame.Display 5   	      PAC_7Frame.MainWindow   5   	   W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35\ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35^ ZWindowsFormsIntegration, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35F 9http://schemas.microsoft.com/winfx/2006/xaml/presentation     R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     5   	   @d2http://schemas.microsoft.com/expression/blend/2008     5   	   Jmc;http://schemas.openxmlformats.org/markup-compatibility/2006     5   	   Owf@clr-namespace:System.Windows.Forms;assembly=System.Windows.Forms     5   	   *localclr-namespace:PAC_7Frame     5   	   4Display clr-namespace:PAC_7Frame.Display     5   	   -   5          � WindowStartupLocation$  CenterScreen=�5
   	    � WindowState$ 	Maximized=�5   	    � Topmost .   � Title$ 
MainWindow��5   	    � ShowInTaskbar .  $
��1024��62   $	��600��6?    � 
ResizeMode$ NoResize=�6L    � WindowStyle$
 None=�6b   .��5      � -   5         �Name$ 	MainFrame��5      $
��1024��6   $	��600��6+   $��Center=�5      $��Center=�6&   ��5   
   �� 5       �� ImageSource$ Res/img_logo.png��6   .+�5   
   "  PAC_7Frame.Display.Displayer  -   5        	  Name$	 Dp_Image��5      $
��Left=�6.   $	��Top=�6I   $	��600��6a   $
��1024��6n      PAC_7Frame.ProgressMessage  5   
   -   5        
  Name$
 
Pg_Loading��5       $��Center=�64   $��56��6Q   $	��256��6]   $	��Top=�6i   $y�	Collapsed=�6�   7  1System.Windows.Forms.Integration.WindowsFormsHost  5   
   -   5          Name$ Wfh_CCTV��5      $��0��6-   $
��1024��6=   $	��600��6J   $
��Left=�6W   $	��Top=�6r   $��0,0,0,0q�6�   $y�	Collapsed=�6�      
Background
 �   �   Child. 5      %   System.Windows.Forms.PictureBox  -   5        Pb_CCTV��5      5      5      5      