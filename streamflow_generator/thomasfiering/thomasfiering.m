function Z=thomasfiering(n_data,n_day,n_per,str1,str2)
%fungsi untuk mencari data bangkitan menggunakan thomas-fiering
%n_day adalah jumlah hari per satu tahap
%per_bangkit adalah periode (kalau n_hari=7, dalam setahun akan ada 
%floor(365/7) atau sekitar 52 tahap. n_per adalah jumlah tahun dibangkitkan
%atau total tahap sama dengan 53*n_per.
%n_data��ʾ��������ݣ�ÿ���������12�У�ÿ�б�ʾһ���£�����һ��excel���棬���¾�������n_day����30Ϊ��
A=tumpukgabungfile(n_data,n_day,str1);%n_data������ݣ�ÿn_dayȡһ��ƽ������һ�������һ�У�����һ������

%pembangkitan selama periode bangkit per_bangkit
AX=A;
for j=1:n_per%n_per�Ǿ������ɶ������ģ�⾶��
[m,n]=size(A);
[q_mean,q_std,rj,bj]=mat_thomas(A);%thomasfiering��ʽ��rj���൱��beta_t
xj=sqrt(1-rj.^2);
t=rand(1,n);%��׼��̬�ֲ�����t
    for i=1:n
        if i==1
        B(:,i)=q_mean(i)+(bj(n)*(AX(n)-q_mean(n)))+(t(n)*q_std(i)*t(i)*xj(n));    
        else
        B(:,i)=q_mean(i)+(bj(i-1)*(B(i-1)-q_mean(i-1)))+(t(i-1)*q_std(i)*t(i)*xj(i-1));
        end
    end
AX=vertcat(AX,B);
end
Z=AX;%���õ�����Bֵ��ǰ���������ԭ���ݣ�����B�ۼƵ��������ɵ�
ZX=vektorderetbaris(Z);%�����������ų�һ����������Ϊ�˻�ͼ

%memasukkan matriks pembanding
sprintf('===memasukkan file pembanding===')
Y=tumpukgabungfile(n_per,n_day,str2);
YY=vertcat(A,Y);
YX=vektorderetbaris(YY);
%��ԭ����ȡ����
[o,p]=size(ZX);
N=[1:o]';
M=[N,ZX,YX]
plot(N,ZX,N,YX)%��ͼ��Ч��
