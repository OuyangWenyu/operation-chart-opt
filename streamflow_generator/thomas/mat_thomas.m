function [q_mean,q_std,rj,bj]=mat_thomas(A)
%fungsi untuk menghasilkan data yang dibutuhkan untuk thomas-fiering
[m,n]=size(A);
q_mean=mean(A);%ÿ��Ԫ�صľ�ֵ������һ��
q_std=std(A);%ÿ��Ԫ�صı�׼�����һ��
for i=1:n
    if i==1
        mat_corr=corrcoef(A(:,n),A(:,i));%��1�к͵�n��(�����һ��)���ݵ����ϵ������
        rj(:,i)=mat_corr(2);%ȡ�ڶ��е�һ�е�����
    else
    mat_corr=corrcoef(A(:,i-1),A(:,i));
    rj(:,i)=mat_corr(2);
    end
end
rj;%���ri��һ�����ϵ������ʾ������֮������ϵ��
for j=1:n
    if j==n
        bj(:,j)=rj(:,j)*q_std(:,1)/q_std(:,j);%����֮������ϵ��*�ڶ��еı�׼��/��һ�еı�׼��
    else
    bj(:,j)=rj(:,j)*q_std(:,j+1)/q_std(:,j);
    end
end
bj;%���bj��һ����