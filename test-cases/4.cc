app teste4
  var
    integer total, soma, teste4;
    real i, j, k, a
  init
      read (i);
      read (j);
      read (k);
      a := 10;
      i := 4 * (5-3 * 50 / 10);
      j := i * 10;
      write(i);
      write(j);
      if ((k > j) || ((k < i) && (k < 100))) then
        k:= j+k;
        write(k)
      end
return