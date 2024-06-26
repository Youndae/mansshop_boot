import React, {useState, useEffect} from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";
import {useDispatch, useSelector} from "react-redux";
import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import {numberComma} from "../../../modules/numberCommaModule";
import {useNavigate} from "react-router-dom";


/*
    기간별 매출.
    가장 기본적으로 현재 년도의 월별 매출을 테이블 구조로 출력.
    테이블 오른쪽 상단의 select box는 현재 년도 포함 3개 년도를 선택할 수 있도록 처리.

    출력되는 월 매출의 정보로는
    월 | 월매출 | 월 상품 출고량 | 월 주문량

    테이블 하단에는 해당 연도의 연매출, 연 상품 출고량, 연 주문량을 출력.

    테이블에서 월 데이터를 클릭하면 상세 페이지로 이동해 해당 월 매출을 좀 더 상세하게 확인할 수 있도록 한다.
 */
function AdminPeriodSales() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [yearSalesData, setYearSalesData] = useState({
        sales: 0,
        salesQuantity: 0,
        orderQuantity: 0,
    });
    const [data, setData] = useState([]);
    const [selectYear, setSelectYear] = useState(0);
    const [selectBoxData, setSelectBoxData] = useState([]);

    const navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        const currentDate = new Date();
        let year = currentDate.getFullYear();
        setSelectYear(year);
        getSalesData(year);

        const yearArr = [];
        for(let i = 0; i < 3; i++)
            yearArr.push(year--);

        setSelectBoxData(yearArr);
    }, []);

    const getSalesData = async (year) => {

        await axiosInstance.get(`admin/sales/period/${year}`)
            .then(res => {

                const content = res.data.content;
                setYearSalesData({
                    sales: content.sales,
                    salesQuantity: content.salesQuantity,
                    orderQuantity: content.orderQuantity,
                });

                setData(content.content);


                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleSelectOnChange = (e) => {
        const year = e.target.value;

        setSelectYear(year);
        getSalesData(year);
    }

    const handleMonthOnClick = (month) => {
        const yearMonth = `${selectYear}-${month}`;

        navigate(`/admin/sales/period/${yearMonth}`);
    }


    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'sales'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>기간별 매출</h1>
                </div>
                <div className="admin-content-content">
                    <div className="admin-content-content-header admin-period-list-header">
                        <h3>{selectYear}년 매출</h3>
                        <select className={'admin-period-select-box'} value={selectYear} onChange={handleSelectOnChange}>
                            {selectBoxData.map((year, index) => {
                                return (
                                    <option key={index} value={year}>{year}</option>
                                )
                            })}
                        </select>
                    </div>
                    <table className="admin-content-table admin-period-table">
                        <thead>
                            <tr>
                                <th>월</th>
                                <th>매출</th>
                                <th>상품 출고량</th>
                                <th>주문량</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map((monthSales, index) => {
                                return (
                                    <tr key={index} onClick={() => handleMonthOnClick(monthSales.date)}>
                                        <td>{monthSales.date}</td>
                                        <td>{numberComma(monthSales.sales)}</td>
                                        <td>{numberComma(monthSales.salesQuantity)}</td>
                                        <td>{numberComma(monthSales.orderQuantity)}</td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </table>
                    <div className="admin-content-year-sales">
                        <p>{selectYear}년 매출 : {numberComma(yearSalesData.sales)}</p>
                        <p>{selectYear}년 판매량 : {numberComma(yearSalesData.salesQuantity)}</p>
                        <p>{selectYear}년 주문량 : {numberComma(yearSalesData.orderQuantity)}</p>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AdminPeriodSales;