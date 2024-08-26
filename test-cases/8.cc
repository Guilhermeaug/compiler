app SomaPares
  var
    integer n, i, soma;
    real z
  init
    soma := 0;
    i := 1;
    read(n);
    repeat
      if (i / 2 = 0) then
        soma := soma + i
      end;
      i := i + 1
    until (i > n);
    write({Soma dos pares: });
    write(soma)
return
