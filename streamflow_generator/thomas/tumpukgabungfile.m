function Z=tumpukgabungfile(n_data,n_day)
%fungsi menggabungkan file-file
%ditumpuk
%n_data������ݣ�ÿn_dayȡһ��ƽ������һ�������һ�У�����һ������
%
f=[];
for i=1:n_data
    sprintf('Masukkan file ke-%d:',i)
    a = input('File:','s');
    b=urutbuangnan(a);%��excel�ļ��ж�ȡ���ݾ���Excel��ʽ������xls��xlsx�ȣ���Ȼ��ת������������������ֵ��������b
    c=kabisat2biasa(b);%���괦��2�µ����һ�죬����������������ȡƽ����Ϊһ�죬bת��Ϊc
    d=hari2periode(c,n_day);%ÿ��n_day����ȡ��n_day������ƽ��ֵ����������d
    e=d';%ȡת�õ�e��Ϊһ��
    f=vertcat(f,e);%��ֵ�ѵ�f������չ���һ�����
end
Z=f;