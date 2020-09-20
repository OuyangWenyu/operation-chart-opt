function Z=kabisat2biasa(vektor)
%fungsi untuk mengubah data tahun kabisat menjadi 365 hari dengan membuang
%data 29 februari dan mengubah data 28 februari menjadi rerata dari tanggal
%28 dan dan 29 februrari

[m,n]=size(vektor);
if m==365,
    for i=1:m
    Z(i)=vektor(i);
    end
else if m==366
    for i=1:(31+27)
        Z(i)=vektor(i)
    end
    Z(31+28)=(vektor(31+28)+vektor(31+29))/2
    for i=(31+30):366
    Z(i-1)=vektor(i)
    end
 else
    sprintf('Datanya kurang mas')
    end
 Z;    
    