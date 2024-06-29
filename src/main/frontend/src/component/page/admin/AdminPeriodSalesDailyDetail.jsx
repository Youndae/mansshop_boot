import React, {useState, useEffect} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams, useSearchParams} from "react-router-dom";
import AdminSideNav from "../../ui/nav/AdminSideNav";
import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber,
    pageSubmit,
    productDetailPagingObject
} from "../../../modules/pagingModule";
import {numberComma} from "../../../modules/numberCommaModule";
import Paging from "../../ui/Paging";

/*
    일 주문 내역

    당일의 주문내역만을 출력한다.
 */
function AdminPeriodSalesDailyDetail() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
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
    const dispatch = useDispatch();

    useEffect(() => {
        getDailyOrderList();
    }, [selectDate]);

    const getDailyOrderList = async () => {

        await axiosInstance.get(`admin/sales/period/order-list?term=${selectDate}&page=${page}`)
            .then(res => {

                setData(res.data.content);

                const pagingObject = productDetailPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    totalElements: res.data.totalElements,
                    activeNo: page,
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handlePageBtn = (e) => {
        pageSubmit(getClickNumber(e), navigate);
        // handlePagingSubmit(getClickNumber(e));
    }

    const handlePagePrev = () => {
        pageSubmit(getPrevNumber(pagingData), navigate);
        // handlePagingSubmit(getPrevNumber(pagingData));
    }

    const handlePageNext = () => {
        pageSubmit(getNextNumber(pagingData));
        // handlePagingSubmit(getNextNumber(pagingData));
    }

    /*const handlePagingSubmit = (pageNum) => {
        navigate(`admin/sales/period/detail/daily/${selectDate}?page=${pageNum}`);
    }*/

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