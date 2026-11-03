const api='/api';
const state={products:[],categories:[],suppliers:[],clients:[],sales:[],toastTimer:null};
const $=(selector,root=document)=>root.querySelector(selector);
const $$=(selector,root=document)=>[...root.querySelectorAll(selector)];

async function request(path,options={}){
  const response=await fetch(api+path,{headers:{'Content-Type':'application/json'},...options});
  if(!response.ok){let body={};try{body=await response.json()}catch{}throw new Error(body.mensaje||`La solicitud falló (${response.status}).`)}
  if(response.status===204)return null;
  return response.json();
}
function escapeHtml(value){return String(value??'').replace(/[&<>"']/g,char=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]))}
function money(value){return new Intl.NumberFormat('es-CO',{style:'currency',currency:'COP',maximumFractionDigits:0}).format(value||0)}
function formValues(form){return Object.fromEntries(new FormData(form).entries())}
function showMessage(text,isError=false){
  const toast=$('#message');toast.textContent=text;toast.classList.toggle('error',isError);toast.classList.add('show');
  clearTimeout(state.toastTimer);state.toastTimer=setTimeout(()=>toast.classList.remove('show'),4200);
}
function fillOptions(selector,items,placeholder,labelFn){
  const markup=`<option value="">${escapeHtml(placeholder)}</option>`+items.map(item=>`<option value="${item.id}">${escapeHtml(labelFn(item))}</option>`).join('');
  $$(selector).forEach(select=>{const previous=select.value;select.innerHTML=markup;if(items.some(item=>String(item.id)===previous))select.value=previous;});
}
function refreshSelectors(){
  fillOptions('.category-options',state.categories,state.categories.length?'Elige una categoría':'Crea una categoría primero',item=>item.nombre);
  fillOptions('.supplier-options',state.suppliers,state.suppliers.length?'Elige un proveedor':'Crea un proveedor primero',item=>item.nombre);
  fillOptions('.client-options',state.clients,state.clients.length?'Elige un cliente':'Crea un cliente primero',item=>`${item.nombre} · ${item.documento}`);
  $$('.sale-product').forEach(select=>{const previous=select.value;select.innerHTML=`<option value="">${state.products.length?'Elige un producto':'Crea un producto primero'}</option>`+state.products.map(item=>`<option value="${item.id}">${escapeHtml(item.nombre)} · ${money(item.precio)} · ${item.stock} uds.</option>`).join('');if(state.products.some(item=>String(item.id)===previous))select.value=previous;});
}
function renderProducts(){
  const query=$('#productSearch').value.trim().toLocaleLowerCase('es');const lowOnly=$('#stockFilter').value==='bajo';
  const visible=state.products.filter(item=>(!query||item.nombre.toLocaleLowerCase('es').includes(query)||item.categoria.toLocaleLowerCase('es').includes(query))&&(!lowOnly||item.stock<=10));
  $('#productRows').innerHTML=visible.map(item=>`<tr><td>${escapeHtml(item.nombre)}</td><td>${escapeHtml(item.categoria)}</td><td>${money(item.precio)}</td><td>${item.stock}</td><td><span class="stock-badge ${item.stock<=10?'low':''}">${item.stock<=10?'Por reponer':'Disponible'}</span></td></tr>`).join('');
  $('#inventoryEmpty').classList.toggle('hidden',visible.length>0);
}
function renderLowStock(){
  const low=state.products.filter(item=>item.stock<=10).sort((a,b)=>a.stock-b.stock).slice(0,5);
  $('#lowList').innerHTML=low.length?low.map(item=>`<div class="low-item"><span>${escapeHtml(item.nombre)}</span><span>${item.stock} uds.</span></div>`).join(''):'<span class="muted">Todo en orden por ahora.</span>';
}
function renderCounts(){
  $('#countProducts').textContent=state.products.length;$('#countLow').textContent=state.products.filter(item=>item.stock<=10).length;
  $('#countClients').textContent=state.clients.length;$('#countSales').textContent=state.sales.length;
}
async function loadAll(quiet=false){
  const connection=$('.connection');
  try{
    const [products,categories,suppliers,clients,sales]=await Promise.all([request('/productos'),request('/categorias'),request('/proveedores'),request('/clientes'),request('/ventas')]);
    Object.assign(state,{products,categories,suppliers,clients,sales});refreshSelectors();renderProducts();renderLowStock();renderCounts();connection.classList.add('is-online');$('#connectionText').textContent='SISTEMA ACTIVO';
    if(!quiet)showMessage('Datos actualizados.');
  }catch(error){connection.classList.remove('is-online');$('#connectionText').textContent='SIN CONEXIÓN';if(!quiet)showMessage(`${error.message} Comprueba que Spring Boot y MySQL estén activos.`,true);}
}
function addSaleLine(){
  const row=document.createElement('div');row.className='sale-line';row.innerHTML='<label class="sale-field">Producto<select class="sale-product" required><option value="">Cargando productos…</option></select></label><label class="sale-field quantity-field">Unidades<input class="sale-quantity" type="number" min="1" value="1" required></label><button type="button" class="remove-line" aria-label="Quitar producto" title="Quitar producto">×</button>';
  $('#saleLines').append(row);refreshSelectors();row.querySelector('.remove-line').addEventListener('click',()=>{if($$('.sale-line').length>1)row.remove();updateEstimate();});row.addEventListener('input',updateEstimate);row.addEventListener('change',updateEstimate);updateEstimate();
}
function updateEstimate(){
  const total=$$('.sale-line').reduce((sum,row)=>{const product=state.products.find(item=>String(item.id)===row.querySelector('.sale-product').value);const quantity=Number(row.querySelector('.sale-quantity').value)||0;return sum+(product?product.precio*quantity:0);},0);
  $('#saleEstimate').textContent=money(total);
}

$('#categoryForm').addEventListener('submit',async event=>{
  event.preventDefault();const form=event.currentTarget;const data=formValues(form);
  try{const created=await request('/categorias',{method:'POST',body:JSON.stringify(data)});form.reset();await loadAll(true);$('#productForm [name="categoriaId"]').value=created.id;showMessage(`Categoría «${created.nombre}» creada y lista para usar.`);}
  catch(error){showMessage(error.message,true);}
});
$('#supplierForm').addEventListener('submit',async event=>{
  event.preventDefault();const form=event.currentTarget;const data=formValues(form);if(!data.correo)delete data.correo;
  try{const created=await request('/proveedores',{method:'POST',body:JSON.stringify(data)});form.reset();await loadAll(true);$('#productForm [name="proveedorId"]').value=created.id;showMessage(`Proveedor «${created.nombre}» creado y seleccionado.`);}
  catch(error){showMessage(error.message,true);}
});
$('#productForm').addEventListener('submit',async event=>{
  event.preventDefault();const form=event.currentTarget;const data=formValues(form);data.precio=Number(data.precio);data.stock=Number(data.stock);data.categoriaId=Number(data.categoriaId);data.proveedorId=Number(data.proveedorId);
  try{const created=await request('/productos',{method:'POST',body:JSON.stringify(data)});form.reset();await loadAll(true);showMessage(`«${created.nombre}» ya está en el inventario.`);}
  catch(error){showMessage(error.message,true);}
});
$('#clientForm').addEventListener('submit',async event=>{
  event.preventDefault();const form=event.currentTarget;const data=formValues(form);
  try{const created=await request('/clientes',{method:'POST',body:JSON.stringify(data)});form.reset();await loadAll(true);$('#saleForm [name="clienteId"]').value=created.id;showMessage(`Cliente «${created.nombre}» guardado y seleccionado para la venta.`);}
  catch(error){showMessage(error.message,true);}
});
$('#saleForm').addEventListener('submit',async event=>{
  event.preventDefault();const form=event.currentTarget;const clienteId=Number($('#saleForm [name="clienteId"]').value);const detalles=$$('.sale-line').map(row=>({productoId:Number(row.querySelector('.sale-product').value),cantidad:Number(row.querySelector('.sale-quantity').value)}));
  try{const sale=await request('/ventas',{method:'POST',body:JSON.stringify({clienteId,detalles})});form.reset();$('#saleLines').innerHTML='';addSaleLine();await loadAll(true);showMessage(`Venta #${sale.id} registrada por ${money(sale.total)}. Inventario actualizado.`);}
  catch(error){showMessage(error.message,true);}
});

$('#addSaleLine').addEventListener('click',addSaleLine);$('#productSearch').addEventListener('input',renderProducts);$('#stockFilter').addEventListener('change',renderProducts);$('#refreshButton').addEventListener('click',()=>loadAll());
const pageSections=[...document.querySelectorAll('#inicio,#inventario,#gestion')];
const sectionLinks=$$('.main-nav a');
if('IntersectionObserver' in window){
  const sectionObserver=new IntersectionObserver(entries=>{
    const visible=entries.filter(entry=>entry.isIntersecting).sort((a,b)=>b.intersectionRatio-a.intersectionRatio)[0];
    if(!visible)return;
    const sectionId=visible.target.id;document.body.dataset.currentSection=sectionId;
    sectionLinks.forEach(link=>{if(link.hash===`#${sectionId}`)link.setAttribute('aria-current','location');else link.removeAttribute('aria-current');});
  },{rootMargin:'-28% 0px -58% 0px',threshold:[0,.15,.35,.6]});
  pageSections.forEach(section=>sectionObserver.observe(section));
}
addSaleLine();loadAll(true);
