for i=1:50
    str=num2str(1953+i);
    suffix1='.xlsx';
    suffix2='ģ��.xlsx';
    str1=[str,suffix1];
    Z=thomasfiering(3,30,1,str1,str1);
    path='C:\Users\asus\Desktop\������ʷ����\ģ�⾶��\';
    str2=[str,suffix2];
    file=[path,str2];
    xlswrite(file,Z);
end
    