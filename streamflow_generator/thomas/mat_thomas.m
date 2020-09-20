function [q_mean,q_std,rj,bj]=mat_thomas(A)
%fungsi untuk menghasilkan data yang dibutuhkan untuk thomas-fiering
[m,n]=size(A);
q_mean=mean(A);%每列元素的均值，构成一行
q_std=std(A);%每列元素的标准差，构成一行
for i=1:n
    if i==1
        mat_corr=corrcoef(A(:,n),A(:,i));%第1列和第n列(即最后一列)数据的相关系数矩阵
        rj(:,i)=mat_corr(2);%取第二行第一列的数据
    else
    mat_corr=corrcoef(A(:,i-1),A(:,i));
    rj(:,i)=mat_corr(2);
    end
end
rj;%最后ri是一行相关系数，表示相邻列之间的相关系数
for j=1:n
    if j==n
        bj(:,j)=rj(:,j)*q_std(:,1)/q_std(:,j);%两列之间的相关系数*第二列的标准差/第一列的标准差
    else
    bj(:,j)=rj(:,j)*q_std(:,j+1)/q_std(:,j);
    end
end
bj;%最后bj是一行数