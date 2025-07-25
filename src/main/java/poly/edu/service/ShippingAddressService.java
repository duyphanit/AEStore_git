package poly.edu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import poly.edu.dao.ShippingAddressDAO;
import poly.edu.entity.Account;
import poly.edu.entity.ShippingAddress;

@Service
public class ShippingAddressService {
	@Autowired
	private ShippingAddressDAO shippingDAO;

	public List<ShippingAddress> getByAccount(Long accountId) {
        return shippingDAO.findByAccount_Id(accountId);
    }

    public ShippingAddress save(ShippingAddress address) {
        // Nếu là mặc định, cập nhật các địa chỉ khác về false
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<ShippingAddress> list = shippingDAO.findByAccount_Id(address.getAccount().getId());
            for (ShippingAddress a : list) {
                if (!a.getId().equals(address.getId())) {
                    a.setIsDefault(false);
                    shippingDAO.save(a);
                }
            }
        }
        return shippingDAO.save(address);
    }

    public void delete(Long id) {
        shippingDAO.deleteById(id);
    }
}
