app Teste5
  var
integer j, k;
real result
init
  read(j);
  read(k);

  if (k > 0) then
    result := j/k
  else
    result := 0
  end;

  write(result)
return
