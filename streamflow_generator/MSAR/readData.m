function [ A ] = readData( path,sheet)
%READDATA Read Data From Excel and choose m1:m2 rows ,n1:n2 columns 
%  read data from excel to a matrix which has the form:
%  [x(t,tau)],t means the year,tau means the month/decad.
%  input arguments:path means the location of the excel file;sheet means
%  the sheet we want.
%  output arguments: X means the matrix we want
%   Detailed explanation goes here
A=xlsread(path,sheet);

end

