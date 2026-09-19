/**
 * 관리자 요청과 전시 심사 흐름을 담당한다.
 *
 * <p>presentation은 HTTP 요청과 응답을, application은 유스케이스 조율을 담당한다. 전시 상태와 반려 사유는 Display 도메인이 소유하며,
 * Admin에서 전시 엔티티나 저장소를 중복 정의하지 않는다. 하위 계층은 실제 구현이 필요할 때 추가한다.
 *
 * <p>연동 범위와 API 초안은 docs/admin_api_contract.md를 따른다.
 */
package com.example.demo.domain.admin;
