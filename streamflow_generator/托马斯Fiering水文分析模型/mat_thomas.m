function [q_mean,q_std,rj,bj]=mat_thomas(A)
%fungsi untuk menghasilkan data yang dibutuhkan untuk thomas-fiering
[m,n]=size(A);
q_mean=mean(A);
q_std=std(A);
for i=1:n
    if i==1
        mat_corr=corrcoef(A(:,n),A(:,i));
        rj(:,i)=mat_corr(2);
    else
    mat_corr=corrcoef(A(:,i-1),A(:,i));
    rj(:,i)=mat_corr(2);
    end
end
rj;
for j=1:n
    if j==n
        bj(:,j)=rj(:,j)*q_std(:,1)/q_std(:,j);
    else
    bj(:,j)=rj(:,j)*q_std(:,j+1)/q_std(:,j);
    end
end
bj;    