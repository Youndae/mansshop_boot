export const SIDE_MENU_MAP = {
    product: [
        { link: '/admin/product', text: '상품 목록' },
        { link: '/admin/product/stock', text: '재고 관리' },
        { link: '/admin/product/discount', text: '할인 설정' },
    ],
    order: [
        { link: '/admin/order', text: '미처리 목록' },
        { link: '/admin/order/all', text: '전체 목록' },
    ],
    qna: [
        { link: '/admin/qna/product', text: '상품 문의' },
        { link: '/admin/qna/member', text: '회원 문의' },
        { link: '/admin/qna/classification', text: '문의 카테고리 설정' },
    ],
    review: [
        { link: '/admin/review', text: '미답변 목록' },
        { link: '/admin/review/all', text: '전체 목록' },
    ],
    sales: [
        { link: '/admin/sales/period', text: '기간별 매출' },
        { link: '/admin/sales/product', text: '상품별 매출' },
    ],
    data: [
        { link: '/admin/data/queue', text: '실패 메시지 관리'},
        { link: '/admin/data/order', text: '실패 주문 관리'}
    ]
}