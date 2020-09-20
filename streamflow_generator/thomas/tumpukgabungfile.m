function Z=tumpukgabungfile(n_data,n_day)
%fungsi menggabungkan file-file
%ditumpuk
%n_data年的数据，每n_day取一个平均数，一年的数成一行，构成一个矩阵
%
f=[];
for i=1:n_data
    sprintf('Masukkan file ke-%d:',i)
    a = input('File:','s');
    b=urutbuangnan(a);%从excel文件中读取数据矩阵（Excel格式可以是xls、xlsx等），然后转换成列向量，最后把数值存入向量b
    c=kabisat2biasa(b);%闰年处理2月的最后一天，把闰年二月最后两天取平均做为一天，b转换为c
    d=hari2periode(c,n_day);%每隔n_day个数取这n_day个数的平均值，构成向量d
    e=d';%取转置得e，为一行
    f=vertcat(f,e);%把值堆到f最后，最终构成一组矩阵
end
Z=f;