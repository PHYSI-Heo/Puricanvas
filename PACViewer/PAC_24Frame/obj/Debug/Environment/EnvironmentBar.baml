   M S B A M L   `   `   `  ����F  BPAC_24Frame, Version=1.0.0.0, Culture=neutral, PublicKeyToken=nullA%clr-namespace:PAC_24Frame.EnvironmentPAC_24Frame.Environment  5      ,    &PAC_24Frame.Environment.EnvironmentBar   5      W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35\ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35^ ZWindowsFormsIntegration, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35F 9http://schemas.microsoft.com/winfx/2006/xaml/presentation     5      R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     5      Jmc;http://schemas.openxmlformats.org/markup-compatibility/2006     5      @d2http://schemas.microsoft.com/expression/blend/2008     5      7local%clr-namespace:PAC_24Frame.Environment     5      $
��1910��5   7   $��48��6D   .��5	      � $
��1910��6   $��48��6     J� Opacity$	  0.8Q�6$    � ColumnDefinitions 5
   
   �� 5      $	��59* �6   �� 5      $	��88* �6   ��5   
   �� 5       �� EndPoint$ 0.5,1&�6"    �� 
StartPoint$ 0.5,0&�63   .��5      �  � Color$ 	#FF363434��6    � Offset$	 0.8Q�61   � 5      $ Transparent��6   $ 0Q�63   5      .+�5   
   �� -   5         ��Name$ 
Ic_Weather��5   >    �� BitmapScalingMode$ HighQuality=�6   $��Icon/ic_clear_sky.png��6R   $��32��6q   $��32��6|   $
��Left=�6�   $��40,8,0,8q�6�   �� 5   
   -   5         ��Name$ 
Tb_Weather��5      $
��Left=�6(   $��--��6C   $��
72,13,0,13q�6M   $��Center=�6a   $��72��5      $��16(�6   $
��Bold"�6-   	 �� TextAlignment$	 Center=�6?   
�������6V   �� 5   
   -   5        $ Ic_Temperature��5   >   $ HighQuality=�6   $��Icon/ic_temperature.png��6V   $��32��6w   $��32��6�   $
��Left=�6�   $��	160,8,0,8q�6�   �� 5   
   -   5        $ Tb_Temperature��5      $
��Left=�6,   $��-- ℃��6G   $��192,13,0,13q�6S   $��Center=�6h   $��96��5      $��16(�6   $
��Bold"�6-   $	 Center=�6?   
�������6V   �� 5   
   -   5        $ Ic_Humidity��5   >   $ HighQuality=�6   $��Icon/ic_humidity.png��6S   $��32��6q   $��32��6|   $
��Left=�6�   $��	296,8,0,8q�6�   �� 5   
   -   5        $ Tb_Humidity��5      $
��Left=�6)   $
��-- %��6D   $��328,13,0,13q�6P   $��Center=�6e   $��96��5      $��16(�6   $
��Bold"�6-   $	 Center=�6?   
�������6V   �� 5!   
   -   5        $ Ic_P_Dust_PM25��5!   >   $ HighQuality=�6   $��Icon/ic_dust_pm25.png��6V   $��32��6u   $��32��6�   $
��Left=�6�   $��	432,8,0,8q�6�   �� 5"   
   -   5        $ Tb_P_Dust_PM25��5"      $
��Left=�6,   $��
-- ㎍/㎥��6G   $��464,13,0,13q�6U   $��Center=�6j   $��96��5#      $��16(�6   $
��Bold"�6-   $	 Center=�6?   
�������6V   �� 5%   
   -	   5        $ Ic_Voc��5%   >   $ HighQuality=�6   $��Icon/ic_voc.png��6N   $��32��6g   $��32��6r   $��Right=�6~   $��	0,8,144,8q�6�   $��1��6�   �� 5&   
   -
   5        $ Tb_Voc��5&      $��Right=�6$   $��-- ppm��6@   $��Center=�6N   $	��112��5'      $��16(�6    $
��Bold"�6.   $	 Center=�6@   $��
0,13,32,13q�6W   
�������$��1��6~   �� 5)   
   -   5        $ Ic_S_Dust_PM25��5)   >   $ HighQuality=�6   $��Icon/ic_dust_pm25.png��6V   $��32��6u   $��32��6�   $��Right=�6�   $��	0,8,312,8q�6�   $��1��6�   �� 5*   
   -   5        $ Tb_S_Dust_PM25��5*      $��Right=�6,   $��
-- ㎍/㎥��6H   $��Center=�6V   $	��112��5+      $��16(�6    $
��Bold"�6.   $	 Center=�6@   $��0,13,200,13q�6W   
�������$��1��6   �� 5.   
   -   5        $ Tb_DateTime��5.      $��2020/08/29        17:21��6)   $��16(�6H   $
��Bold"�6V   
�������$	 Center=�5/      $��560,13,344,13q�6+   $��Center=�6B   $��2��6]   51      52      