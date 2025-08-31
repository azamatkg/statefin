package kg.infosystems.statefin.service.impl;

import kg.infosystems.statefin.dto.request.reference.RepaymentOrderCreateRequest;
import kg.infosystems.statefin.dto.request.reference.RepaymentOrderSearchRequest;
import kg.infosystems.statefin.dto.update.reference.RepaymentOrderUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.RepaymentOrderResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.RepaymentOrder;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.repository.RepaymentOrderRepository;
import kg.infosystems.statefin.service.RepaymentOrderService;
import kg.infosystems.statefin.specification.RepaymentOrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepaymentOrderServiceImpl implements RepaymentOrderService {

    private final RepaymentOrderRepository repaymentOrderRepository;

    @Override
    @Transactional
    public RepaymentOrderResponse createRepaymentOrder(RepaymentOrderCreateRequest createRepaymentOrderDto) {

        if (repaymentOrderRepository.existsByNameRu(createRepaymentOrderDto.getNameRu())) {
            throw new IllegalArgumentException("RepaymentOrder with nameRu " + createRepaymentOrderDto.getNameRu() + " already exists");
        }

        RepaymentOrder repaymentOrder = RepaymentOrder.builder()
                .nameEn(createRepaymentOrderDto.getNameEn())
                .nameRu(createRepaymentOrderDto.getNameRu())
                .nameKg(createRepaymentOrderDto.getNameKg())
                .description(createRepaymentOrderDto.getDescription())
                .status(createRepaymentOrderDto.getStatus() != null ? createRepaymentOrderDto.getStatus() : ReferenceEntityStatus.ACTIVE)
                .build();

        RepaymentOrder savedRepaymentOrder = repaymentOrderRepository.save(repaymentOrder);

        return mapToResponseDto(savedRepaymentOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public RepaymentOrderResponse getRepaymentOrderById(Long id) {
        RepaymentOrder repaymentOrder = repaymentOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepaymentOrder not found with id: " + id));
        return mapToResponseDto(repaymentOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepaymentOrderResponse> getAllRepaymentOrders(Pageable pageable) {
        return repaymentOrderRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepaymentOrderResponse> getAllActiveRepaymentOrders(Pageable pageable) {
        return repaymentOrderRepository.findByStatusOrderByNameRuAsc(ReferenceEntityStatus.ACTIVE, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepaymentOrderResponse> searchRepaymentOrders(String searchTerm, Pageable pageable) {

        Specification<RepaymentOrder> spec = RepaymentOrderSpecification.searchByTerm(searchTerm);
        return repaymentOrderRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepaymentOrderResponse> searchRepaymentOrders(String searchTerm, ReferenceEntityStatus status, Pageable pageable) {

        Specification<RepaymentOrder> spec = RepaymentOrderSpecification.searchByTermAndStatus(searchTerm, status);
        return repaymentOrderRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public RepaymentOrderResponse updateRepaymentOrder(Long id, RepaymentOrderUpdateRequest updateRepaymentOrderDto) {
        RepaymentOrder repaymentOrder = repaymentOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepaymentOrder not found with id: " + id));

        if (updateRepaymentOrderDto.getNameEn() != null) {
            repaymentOrder.setNameEn(updateRepaymentOrderDto.getNameEn());
        }
        if (updateRepaymentOrderDto.getNameRu() != null) {
            if (!repaymentOrder.getNameRu().equals(updateRepaymentOrderDto.getNameRu()) &&
                    repaymentOrderRepository.existsByNameRu(updateRepaymentOrderDto.getNameRu())) {
                throw new IllegalArgumentException("RepaymentOrder with nameRu " + updateRepaymentOrderDto.getNameRu() + " already exists");
            }
            repaymentOrder.setNameRu(updateRepaymentOrderDto.getNameRu());
        }
        if (updateRepaymentOrderDto.getNameKg() != null) {
            repaymentOrder.setNameKg(updateRepaymentOrderDto.getNameKg());
        }
        if (updateRepaymentOrderDto.getDescription() != null) {
            repaymentOrder.setDescription(updateRepaymentOrderDto.getDescription());
        }
        if (updateRepaymentOrderDto.getStatus() != null) {
            repaymentOrder.setStatus(updateRepaymentOrderDto.getStatus());
        }

        RepaymentOrder updatedRepaymentOrder = repaymentOrderRepository.save(repaymentOrder);
        return mapToResponseDto(updatedRepaymentOrder);
    }

    @Override
    @Transactional
    public void deleteRepaymentOrder(Long id) {
        RepaymentOrder repaymentOrder = repaymentOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepaymentOrder not found with id: " + id));

//        if (repaymentOrderRepository.isReferencedByCreditPrograms(id)) {
//            throw new IllegalStateException("RepaymentOrder with ID " + id + " cannot be deleted as it is referenced by credit programs");
//        }

        repaymentOrderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RepaymentOrderResponse activateRepaymentOrder(Long id) {
        RepaymentOrder repaymentOrder = repaymentOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepaymentOrder not found with id: " + id));

        repaymentOrder.setStatus(ReferenceEntityStatus.ACTIVE);
        RepaymentOrder updatedRepaymentOrder = repaymentOrderRepository.save(repaymentOrder);
        return mapToResponseDto(updatedRepaymentOrder);
    }

    @Override
    @Transactional
    public RepaymentOrderResponse deactivateRepaymentOrder(Long id) {
        RepaymentOrder repaymentOrder = repaymentOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepaymentOrder not found with id: " + id));

        repaymentOrder.setStatus(ReferenceEntityStatus.INACTIVE);
        RepaymentOrder updatedRepaymentOrder = repaymentOrderRepository.save(repaymentOrder);
        return mapToResponseDto(updatedRepaymentOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameRu(String nameRu) {
        return repaymentOrderRepository.existsByNameRu(nameRu);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public boolean isReferencedByCreditPrograms(Long id) {
//        return repaymentOrderRepository.isReferencedByCreditPrograms(id);
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepaymentOrderResponse> getRepaymentOrdersByStatus(ReferenceEntityStatus status, Pageable pageable) {
        return repaymentOrderRepository.findByStatusOrderByNameRuAsc(status, pageable)
                .map(this::mapToResponseDto);
    }

    private RepaymentOrderResponse mapToResponseDto(RepaymentOrder repaymentOrder) {
//        boolean isReferenced = repaymentOrderRepository.isReferencedByCreditPrograms(repaymentOrder.getId());

        return RepaymentOrderResponse.builder()
                .id(repaymentOrder.getId())
                .version(repaymentOrder.getVersion())
                .nameEn(repaymentOrder.getNameEn())
                .nameRu(repaymentOrder.getNameRu())
                .nameKg(repaymentOrder.getNameKg())
                .description(repaymentOrder.getDescription())
                .status(repaymentOrder.getStatus())
                .createdAt(repaymentOrder.getCreatedAt())
                .updatedAt(repaymentOrder.getUpdatedAt())
                .createdByUsername(repaymentOrder.getCreatedBy() != null ? repaymentOrder.getCreatedBy().getUsername() : null)
                .updatedByUsername(repaymentOrder.getUpdatedBy() != null ? repaymentOrder.getUpdatedBy().getUsername() : null)
//                .isReferencedByCreditPrograms(isReferenced)
                .build();
    }
}