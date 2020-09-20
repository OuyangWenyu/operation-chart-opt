for i=1:50
    str=num2str(1953+i);
    suffix1='.xlsx';
    suffix2='模拟.xlsx';
    str1=[str,suffix1];
    Z=thomasfiering(3,30,1,str1,str1);
    path='C:\Users\asus\Desktop\各年历史径流\模拟径流\';
    str2=[str,suffix2];
    file=[path,str2];
    xlswrite(file,Z);
end
    