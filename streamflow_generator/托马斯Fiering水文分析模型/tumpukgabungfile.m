function Z=tumpukgabungfile(n_data,n_day)
%fungsi menggabungkan file-file
%ditumpuk
f=[];
for i=1:n_data
    sprintf('Masukkan file ke-%d:',i)
    a = input('File:','s');
    b=urutbuangnan(a);
    c=kabisat2biasa(b);
    d=hari2periode(c,n_day);
    e=d';
    f=vertcat(f,e);
end
Z=f;