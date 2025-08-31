package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.reference.RepaymentOrderCreateRequest;
import kg.infosystems.statefin.dto.request.reference.RepaymentOrderSearchRequest;
import kg.infosystems.statefin.dto.update.reference.RepaymentOrderUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.RepaymentOrderResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RepaymentOrderService {

    RepaymentOrderResponse createRepaymentOrder(RepaymentOrderCreateRequest createRepaymentOrderDto);

    RepaymentOrderResponse getRepaymentOrderById(Long id);

    Page<RepaymentOrderResponse> getAllRepaymentOrders(Pageable pageable);

    Page<RepaymentOrderResponse> getAllActiveRepaymentOrders(Pageable pageable);

    Page<RepaymentOrderResponse> searchRepaymentOrders(String searchTerm, Pageable pageable);

    Page<RepaymentOrderResponse> searchRepaymentOrders(String searchTerm, ReferenceEntityStatus status, Pageable pageable);

    RepaymentOrderResponse updateRepaymentOrder(Long id, RepaymentOrderUpdateRequest updateRepaymentOrderDto);

    void deleteRepaymentOrder(Long id);

    RepaymentOrderResponse activateRepaymentOrder(Long id);

    RepaymentOrderResponse deactivateRepaymentOrder(Long id);

    boolean existsByNameRu(String nameRu);

//    boolean isReferencedByCreditPrograms(Long id);

    Page<RepaymentOrderResponse> getRepaymentOrdersByStatus(ReferenceEntityStatus status, Pageable pageable);
}