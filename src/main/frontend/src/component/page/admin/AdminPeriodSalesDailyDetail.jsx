import React, {useState, useEffect} from 'react';
import {useNavigate, useParams, useSearchParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber, mainProductPagingObject,
    pageSubmit
} from "../../../modules/pagingModule";
import {numberComma} from "../../../modules/numberCommaModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import Paging from "../../ui/Paging";

/*
    일 주문 내역

    당일의 주문내역만을 출력한다.
 */
function AdminPeriodSalesDailyDetail() {
    const { selectDate } = useParams();
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');

    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        totalElements: 0,
        activeNo: page,
    });
    const [data, setData] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {
        getDailyOrderList();
    }, [selectDate]);

    //선택 일자 전체 주문 내역 조회
    const getDailyOrderList = async () => {

        await axiosInstance.get(`admin/sales/period/order-list?term=${selectDate}&page=${page}`)
            .then(res => {
                setData(res.data.content);

                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });
            })
    }

    //페이지네이션 버튼 이벤트
    const handlePageBtn = (e) => {
        pageSubmit(getClickNumber(e), navigate);
    }

    //페이지네이션 이전 버튼 이벤트
    const handlePagePrev = () => {
        pageSubmit(getPrevNumber(pagingData), navigate);
    }

    //페이지네이션 다음 버튼 이벤트
    const handlePageNext = () => {
        pageSubmit(getNextNumber(pagingData));
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'sales'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>{selectDate.replaceAll('-', ' / ')} 주문 내역</h1>
                </div>
                <div className="admin-content-content">
                    {data.map((orderData, index) => {
                        return (
                            <div key={index} className="admin-period-order-content">
                                {orderData.detailList.map((detail, index) => {
                                    const sizeText = `사이즈 : ${detail.size}`;
                                    const colorText = `컬러 : ${detail.color}`;
                                    let optionText = '';
                                    if(detail.size === null){
                                        if(detail.color !== null) {
                                            optionText = colorText;
                                        }
                                    }else {
                                        if(detail.color !== null) {
                                            optionText = `${sizeText}, ${colorText}`;
                                        }else {
                                            optionText = sizeText;
                                        }
                                    }
                                    return (
                                        <div key={index} className="form-group">
                                            <label>{detail.productName}</label>
                                            <p>{`${optionText}, 수량 : ${detail.count}, 금액 : ${numberComma(detail.price)}`}</p>
                                        </div>
                                    )
                                })}
                                <p className="admin-period-order-content-total">{`결제 금액 : ${numberComma(orderData.totalPrice)}, 배송비 : ${numberComma(orderData.deliveryFee)}, 결제 방식 : ${orderData.paymentType}`}</p>
                            </div>
                        )
                    })}
                </div>
                <Paging
                    pagingData={pagingData}
                    onClickNumber={handlePageBtn}
                    onClickPrev={handlePagePrev}
                    onClickNext={handlePageNext}
                    className={'like-paging'}
                />
            </div>
        </div>
    )
}

export default AdminPeriodSalesDailyDetail;