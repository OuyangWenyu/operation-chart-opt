function Z=tumpukgabungfile(n_data,n_day,str)
%fungsi menggabungkan file-file
%ditumpuk
%n_data������ݣ�ÿn_dayȡһ��ƽ������һ�������һ�У�����һ������
%
f=[];
initial=str2num(str(1:4));
sprintf('%s','�Զ���excel�����������')
for i=1:n_data
   
    substr=int2str(initial+i);
    tempstr=[substr,'.xlsx'];
   
    b=urutbuangnan(tempstr);%��excel�ļ��ж�ȡ���ݾ���Excel��ʽ������xlsx�ȣ���Ȼ��ת������������������ֵ��������b
    c=kabisat2biasa(b);%���괦��2�µ����һ�죬����������������ȡƽ����Ϊһ�죬bת��Ϊc
    d=hari2periode(c,n_day);%ÿ��n_day����ȡ��n_day������ƽ��ֵ����������d
    e=d';%ȡת�õ�e��Ϊһ��
    f=vertcat(f,e);%��ֵ�ѵ�f������չ���һ�����
end
Z=f;