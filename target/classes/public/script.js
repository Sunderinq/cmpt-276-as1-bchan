const BtnAdd = document.querySelector(".btn-add");

BtnAdd.addEventListener("click", createDiv);

function createDiv ()
  {
  var boxEle = document.createElement('div');
  var container = document.querySelector('.boxContainer');
  boxEle.style.width = '100px';
  boxEle.style.height = '100px';
  boxEle.style.backgroundColor = '#f00';
  container.appendChild(boxEle);
  }