function Z=urutgabungfile(n_data,n_day)
%fungsi menggabungkan file-file
e=[];
for i=1:n_data
    sprintf('Masukkan file ke-%d:',i)
    a = input('File: ', 's');
    b=urutbuangnan(a);
    c=kabisat2biasa(b);
    d=hari2periode(c,n_day);
    e=vertcat(e,d);
end
[m,n]=size(e);
for i=1:m
    f(i)=i;
    Z(i,:)=[f(i) e(i)];
end
Z;