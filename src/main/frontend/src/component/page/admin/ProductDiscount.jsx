import React from 'react';



/*
        할인 설정 컴포넌트.

        두개의 select box가 존재.
        하나는 분류, 두번째는 분류 선택에 따른 상품명

        두개의 select box 선택 후 옆의 상품 추가 버튼을 누르면

        하단에 테이블 구조로 분류와 상품명이 출력.

        테이블 상단에는 input으로 할인율 설정이 가능.

        처리 버튼으로는 할인 설정 이라는 네이밍의 버튼을 추가.

        처음 페이지 접근 시 상품 분류 리스트와 각 분류별 상품 리스트를 받는다.

        {
            classificationList: [
                {
                    id: '',
                    name: '',
                },
                ...
            ],
            productList: [
                classificationName : [
                    {
                        productId: '',
                        productName: '',
                    },
                    ...
                ],
                classificationName : [
                    {
                        productId: '',
                        productName: '',
                    },
                    ...
                ],
                ...
            ]
        }

        state로 classificationList를 관리하고
        각 classification마다 존재하는 상품 리스트는 하나의 state로 관리한다.
        그렇기 때문에 classificationName과 상품 리스트의 객체명은 동일해야 한다.

        서버에 전달하는 처리 body로는 선택된 productId 리스트와 할인율을 전달한다.
     */
function ProductDiscount() {

}

export default ProductDiscount;