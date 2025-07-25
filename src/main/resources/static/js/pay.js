// pay.js - Tổng hợp toàn bộ logic JS cho trang thanh toán

let selectedAddressId = null;

function showConfirmModal() {
	var ship = document.querySelector('input[name="ship"]:checked');
	if (!ship) {
		alert('Vui lòng chọn phương thức giao hàng!');
		return;
	}
	var payment = document.querySelector('input[name="payment"]:checked').value;
	if (payment === 'momo') {
		document.getElementById('qrSection').style.display = 'block';
	} else {
		document.getElementById('qrSection').style.display = 'none';
	}
	var grandTotal = document.getElementById('grand-total').textContent;
	var modalTotal = document.querySelector('#confirmModal .modal-body .text-danger.fs-4.fw-bold');
	if (modalTotal) {
		modalTotal.textContent = grandTotal;
	}
	var modal = new bootstrap.Modal(document.getElementById('confirmModal'));
	modal.show();
}

document.querySelectorAll('input[name="payment"]').forEach(function(radio) {
	radio.addEventListener('change', function() {
		if (this.value === 'momo') {
			document.getElementById('qrSection').style.display = 'block';
		} else {
			document.getElementById('qrSection').style.display = 'none';
		}
	});
});

function loadDefaultAddress() {
	fetch('/shipping-addresses')
		.then(res => res.json())
		.then(data => {
			let addr = null;
			if (selectedAddressId) {
				addr = data.find(a => a.id === selectedAddressId);
			}
			if (!addr) {
				addr = data.find(a => a.isDefault) || data[0];
			}
			if (addr) {
				selectedAddressId = addr.id;
				document.getElementById('shipping-name').textContent = addr.fullName;
				document.getElementById('shipping-phone').textContent = addr.phone;
				document.getElementById('shipping-address').textContent = addr.address;
				document.getElementById('shipping-change-btn').style.display = '';
				updateExpressShippingAvailability();
			} else {
				document.getElementById('shipping-name').textContent = '';
				document.getElementById('shipping-phone').textContent = '';
				document.getElementById('shipping-address').textContent = '';
				document.getElementById('shipping-change-btn').style.display = '';
			}
		});
}

document.addEventListener('DOMContentLoaded', function() {
	loadDefaultAddress();
	function updateInsurance() {
		let total = 0;
		let insuranceTotal = 0;
		document.querySelectorAll('.insurance-checkbox').forEach(function(cb) {
			const price = parseFloat(cb.getAttribute('data-price'));
			const quantity = parseInt(cb.getAttribute('data-quantity'));
			total += price * quantity;
			let fee = 0;
			if (cb.checked) {
				fee = price * 0.02 * quantity;
				insuranceTotal += fee;
			}
			cb.closest('.form-check').querySelector('.insurance-fee').textContent =
				cb.checked ? `+${fee.toLocaleString('vi-VN')} ₫` : '';
		});
		document.getElementById('total-amount').textContent = total.toLocaleString('vi-VN') + ' ₫';
		document.getElementById('total-insurance').textContent = insuranceTotal.toLocaleString('vi-VN') + ' ₫';
		document.getElementById('grand-total').textContent = (total + insuranceTotal).toLocaleString('vi-VN') + ' ₫';
	}
	document.querySelectorAll('.insurance-checkbox').forEach(function(cb) {
		cb.addEventListener('change', updateInsurance);
	});
	updateInsurance();
	updateExpressShippingAvailability();
	let checkedShip = document.querySelector('input[name="ship"]:checked');
	if (!checkedShip) {
		document.getElementById('shipping-options').style.display = 'none';
		updateShippingFee(0);
	}
	updateGrandTotal();
	let shipHome = document.querySelector('input[name="ship"][value="home"]');
	if (shipHome && shipHome.checked) {
		document.getElementById('shipping-options').style.display = 'block';
	} else {
		document.getElementById('shipping-options').style.display = 'none';
		updateShippingFee(0);
	}
	document.getElementById('orderInsuranceCheckbox').addEventListener('change', updateOrderInsurance);
	updateOrderInsurance();
});

function openAddressModal() {
	var modalEl = document.getElementById('addressModal');
	var existingModal = bootstrap.Modal.getInstance(modalEl);
	if (existingModal) {
		existingModal.hide();
	}
	fetch('/shipping-addresses')
		.then(res => res.json())
		.then(data => {
			let html = '';
			data.forEach(addr => {
				html += `<div class="d-flex align-items-center mb-2">
                    <input type="radio" name="addressRadio" value="${addr.id}" ${selectedAddressId === addr.id ? 'checked' : ''} class="me-2">
                    <div class="flex-grow-1">
						<b>${addr.fullName}</b> | ${addr.phone} | ${addr.address}
                        ${!addr.isDefault ? `<button onclick="setDefault(${addr.id})" type="button" class="btn btn-sm btn-outline-secondary ms-2">Đặt làm mặc định</button>` : '<span class="text-success ms-2">(Mặc định)</span>'}
                    </div>
                    <button onclick="editAddress(${addr.id})" type="button" class="btn btn-sm btn-outline-primary ms-2">Sửa</button>
                    <button onclick="deleteAddress(${addr.id})" type="button" class="btn btn-sm btn-outline-danger ms-2">Xóa</button>
                </div>
				<hr>`;
			});
			document.getElementById('addressListContainer').innerHTML = html;
			document.getElementById('addressFormContainer').innerHTML = '';
			document.querySelectorAll('input[name="addressRadio"]').forEach(function(radio) {
				radio.addEventListener('change', function() {
					selectedAddressId = parseInt(this.value);
				});
			});
			var modal = new bootstrap.Modal(modalEl);
			modal.show();
		});
}

function closeAddressModal() {
	var modal = bootstrap.Modal.getInstance(document.getElementById('addressModal'));
	if (modal) modal.hide();
}

function confirmSelectAddress() {
	const checkedRadio = document.querySelector('input[name="addressRadio"]:checked');
	if (checkedRadio) {
		selectedAddressId = parseInt(checkedRadio.value);
		fetch('/shipping-addresses')
			.then(res => res.json())
			.then(data => {
				const addr = data.find(a => a.id === selectedAddressId);
				if (addr) {
					document.getElementById('shipping-name').textContent = addr.fullName;
					document.getElementById('shipping-phone').textContent = addr.phone;
					document.getElementById('shipping-address').textContent = addr.address;
					updateExpressShippingAvailability();
				}
				var modal = document.getElementById('addressModal');
				if (modal.classList.contains('show')) {
					var bsModal = bootstrap.Modal.getInstance(modal);
					bsModal.hide();
				} else {
					modal.style.display = 'none';
				}
			});
	} else {
		alert('Vui lòng chọn một địa chỉ!');
	}
}

function setDefault(id) {
	fetch('/shipping-addresses')
		.then(res => res.json())
		.then(data => {
			let addr = data.find(a => a.id === id);
			addr.isDefault = true;
			fetch('/shipping-addresses', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(addr)
			}).then(() => {
				selectedAddressId = addr.id;
				openAddressModal();
				loadDefaultAddress();
				updateExpressShippingAvailability();
			});
		});
}

function deleteAddress(id) {
	fetch('/shipping-addresses/' + id, { method: 'DELETE' })
		.then(() => {
			if (selectedAddressId === id) selectedAddressId = null;
			openAddressModal();
			loadDefaultAddress();
			updateExpressShippingAvailability();
		});
}

function showAddForm() {
	document.getElementById('addressFormContainer').innerHTML = `
        <form onsubmit="addAddress(event)">
            <div class="mb-2"><input name="fullName" class="form-control" placeholder="Họ tên" required></div>
            <div class="mb-2"><input name="phone" class="form-control" placeholder="Số điện thoại" required></div>
            <div class="mb-2"><input name="address" class="form-control" placeholder="Địa chỉ" required></div>
            <div class="mb-2"><label><input type="checkbox" name="isDefault"> Đặt làm mặc định</label></div>
            <div class="text-end">
                <button type="button" class="btn btn-secondary" onclick="closeAddForm()">Hủy</button>
                <button type="submit" class="btn btn-primary">Lưu</button>
            </div>
        </form>
    `;
	document.getElementById('addressListContainer').style.display = 'none';
}

function closeAddForm() {
	document.getElementById('addressFormContainer').innerHTML = '';
	document.getElementById('addressListContainer').style.display = '';
}

function addAddress(event) {
	event.preventDefault();

	const form = event.target;
	const data = {
		fullName: form.fullName.value,
		phone: form.phone.value,
		address: form.address.value,
		isDefault: form.isDefault.checked
	};

	fetch('/shipping-addresses', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(data)
	})
		.then(() => {
			// Sau khi thêm xong, gọi lại danh sách
			return fetch('/shipping-addresses');
		})
		.then(res => res.json())
		.then(addresses => {
			closeAddForm();
			if (addresses.length > 0) {
				selectedAddressId = addresses[addresses.length - 1].id;
			}
			let html = '';
			addresses.forEach(addr => {
				html += `<div class="d-flex align-items-center mb-2">
				<input type="radio" name="addressRadio" value="${addr.id}" ${selectedAddressId === addr.id ? 'checked' : ''} class="me-2">
				<div class="flex-grow-1">
					<b>${addr.fullName}</b> | ${addr.phone} | ${addr.address}
					${!addr.isDefault ? `<button onclick="setDefault(${addr.id})" type="button" class="btn btn-sm btn-outline-secondary ms-2">Đặt làm mặc định</button>` : '<span class="text-success ms-2">(Mặc định)</span>'}
				</div>
				<button onclick="editAddress(${addr.id})" type="button" class="btn btn-sm btn-outline-primary ms-2">Sửa</button>
				<button onclick="deleteAddress(${addr.id})" type="button" class="btn btn-sm btn-outline-danger ms-2">Xóa</button>
			</div>`;
			});

			document.getElementById('addressListContainer').innerHTML = html;
			document.getElementById('addressListContainer').style.display = '';
			document.querySelectorAll('input[name="addressRadio"]').forEach(function(radio) {
				radio.addEventListener('change', function() {
					selectedAddressId = parseInt(this.value);
				});
			});
			updateExpressShippingAvailability();
		});
}


function editAddress(id) {
	fetch('/shipping-addresses')
		.then(res => res.json())
		.then(data => {
			const addr = data.find(a => a.id === id);
			document.getElementById('addressFormContainer').innerHTML = `
                <form onsubmit="updateAddress(event, ${id})">
                    <div class="mb-2"><input name="fullName" class="form-control" placeholder="Họ tên" value="${addr.fullName}" required></div>
                    <div class="mb-2"><input name="phone" class="form-control" placeholder="Số điện thoại" value="${addr.phone}" required></div>
                    <div class="mb-2"><input name="address" class="form-control" placeholder="Địa chỉ" value="${addr.address}" required></div>
                    <div class="mb-2"><label><input type="checkbox" name="isDefault" ${addr.isDefault ? 'checked' : ''}> Đặt làm mặc định</label></div>
                    <div class="text-end">
                        <button type="button" class="btn btn-secondary" onclick="closeAddForm()">Hủy</button>
                        <button type="submit" class="btn btn-primary">Cập nhật</button>
                    </div>
                </form>
            `;
			document.getElementById('addressListContainer').style.display = 'none';
		});
}

function updateAddress(event, id) {
	event.preventDefault();
	const form = event.target;
	const data = {
		id: id,
		fullName: form.fullName.value,
		phone: form.phone.value,
		address: form.address.value,
		isDefault: form.isDefault.checked
	};
	fetch('/shipping-addresses', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(data)
	}).then(res => res.json())
		.then(addr => {
			selectedAddressId = addr.id;
			closeAddForm();
			openAddressModal();
			loadDefaultAddress();
			updateExpressShippingAvailability();
		});
}

function updateConfirmModalAddress() {
	fetch('/shipping-addresses')
		.then(res => res.json())
		.then(data => {
			const addr = data.find(a => a.id === selectedAddressId);
			if (addr) {
				document.getElementById('confirmFullName').value = addr.fullName;
				document.getElementById('confirmPhone').value = addr.phone;
				document.getElementById('confirmAddress').value = addr.address;
				if (document.getElementById('selectedAddressId'))
					document.getElementById('selectedAddressId').value = addr.id;
			}
		});
}
document.getElementById('confirmModal').addEventListener('show.bs.modal', updateConfirmModalAddress);

function showSuccessAlert() {
	document.getElementById('successModal').style.display = 'block';
	setTimeout(function() {
		document.getElementById('successModal').style.display = 'none';
	}, 5000);
}
function closeSuccessModal() {
	document.getElementById('successModal').style.display = 'none';
}

function updateTransferButton() {
	var payment = document.querySelector('input[name="payment"]:checked').value;
	var btnTransfer = document.getElementById('btnTransferConfirm');
	var btnOrder = document.getElementById('btnOrderConfirm');
	if (payment === 'momo') {
		btnTransfer.style.display = 'inline-block';
		btnTransfer.disabled = true;
		setTimeout(function() {
			btnTransfer.disabled = false;
		}, 60000);
		btnOrder.style.display = 'none';
	} else {
		btnTransfer.style.display = 'none';
		btnTransfer.disabled = false;
		btnOrder.style.display = 'inline-block';
	}
}
document.getElementById('confirmModal').addEventListener('show.bs.modal', updateTransferButton);
document.querySelectorAll('input[name="payment"]').forEach(function(radio) {
	radio.addEventListener('change', updateTransferButton);
});

function showInsurancePolicy() {
	var modal = new bootstrap.Modal(document.getElementById('insurancePolicyModal'));
	modal.show();
}

function updateOrderInsurance() {
	let total = orderTotal;
	let insuranceFee = 0;
	if (document.getElementById('orderInsuranceCheckbox').checked) {
		insuranceFee = total * 0.01;
	}
	document.getElementById('order-insurance-fee').textContent = insuranceFee > 0 ? `+${insuranceFee.toLocaleString('vi-VN')} ₫` : '';
	document.getElementById('total-insurance').textContent = insuranceFee.toLocaleString('vi-VN') + ' ₫';
	document.getElementById('grand-total').textContent = (total + insuranceFee).toLocaleString('vi-VN') + ' ₫';
	document.getElementById('total-amount').textContent = total.toLocaleString('vi-VN') + ' ₫';
}

document.querySelectorAll('input[name="ship"]').forEach(function(radio) {
	radio.addEventListener('change', function() {
		if (this.value === 'home') {
			document.getElementById('shipping-options').style.display = 'block';
			if (!document.querySelector('input[name="shippingMethod"]:checked')) {
				document.querySelector('input[name="shippingMethod"][value="GHN"]').checked = true;
				updateShippingFee(32000);
			}
		} else if (this.value === 'store') {
			document.getElementById('shipping-options').style.display = 'none';
			updateShippingFee(0);
		}
		updateGrandTotal();
	});
});

let shippingFee = 32000;

function updateShippingFee(fee) {
	shippingFee = fee;
	document.getElementById('shipping-fee').textContent = fee.toLocaleString('vi-VN') + ' ₫';
	updateGrandTotal();
}

document.querySelectorAll('input[name="shippingMethod"]').forEach(function(radio) {
	radio.addEventListener('change', function() {
		updateShippingFee(parseInt(this.getAttribute('data-fee')));
	});
});

function updateGrandTotal() {
	let total = orderTotal;
	let insuranceFee = document.getElementById('orderInsuranceCheckbox').checked ? total * 0.01 : 0;
	let grandTotal = total + insuranceFee + shippingFee;
	document.getElementById('grand-total').textContent = grandTotal.toLocaleString('vi-VN') + ' ₫';
}

function updateExpressShippingAvailability() {
	const address = document.getElementById('shipping-address').textContent.trim().toLowerCase();
	const expressRadio = document.querySelector('input[name="shippingMethod"][value="HOA_TOC"]');
	const expressLabel = expressRadio ? expressRadio.closest('label') : null;
	if (!expressRadio || !expressLabel) return;

	if (
		address.includes('tp hcm') ||
		address.includes('tp hồ chí minh') ||
		address.includes('thành phố hồ chí minh')
	) {
		expressRadio.disabled = false;
		expressLabel.classList.remove('shipping-disabled');
	} else {
		expressRadio.disabled = true;
		expressLabel.classList.add('shipping-disabled');
		if (expressRadio.checked) {
			document.querySelector('input[name="shippingMethod"][value="GHN"]').checked = true;
			updateShippingFee(32000);
		}
	}
}
