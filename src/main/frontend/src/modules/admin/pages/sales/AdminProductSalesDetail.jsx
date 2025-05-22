import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import { getProductSalesDetail } from '../../services/adminSalesService';
import { numberComma } from '../../../../common/utils/formatNumberComma';
import { getProductOption } from '../../../../common/utils/productOptionUtils';

import AdminSideNav from '../../components/AdminSideNav';

//상품 매출 상세 페이지
function AdminProductSalesDetail() {
	const { productId } = useParams();

    const [detailData, setDetailData] = useState({
        productName: '',
        totalSales: 0,
        totalSalesQuantity: 0,
        yearSales: 0,
        yearSalesQuantity: 0,
        lastYearComparison: 0,
        lastYearSales: 0,
        lastYearSalesQuantity: 0,
        year: 0,
    });
    const [monthSales, setMonthSales] = useState([]);
    const [optionTotalSales, setOptionTotalSales] = useState([]);
    const [optionYearSales, setOptionYearSales] = useState([]);
    const [optionLastYearSales, setOptionLastYearSales] = useState([]);

	useEffect(() => {
		const getDetail = async () => {
			try {
				const res = await getProductSalesDetail(productId);

				const content = res.data;
                const date = new Date();

                setDetailData({
                    productName: content.productName,
                    totalSales: content.totalSales,
                    totalSalesQuantity: content.totalSalesQuantity,
                    yearSales: content.yearSales,
                    yearSalesQuantity: content.yearSalesQuantity,
                    lastYearComparison: content.lastYearComparison,
                    lastYearSales: content.lastYearSales,
                    lastYearSalesQuantity: content.lastYearSalesQuantity,
                    year: date.getFullYear(),
                });

                setMonthSales(content.monthSales);
                setOptionTotalSales(content.optionTotalSales);
                setOptionYearSales(content.optionYearSales);
                setOptionLastYearSales(content.optionLastYearSales);
			} catch (error) {
				console.log(error);
			}
		}

		getDetail();
	}, [productId]);
	
	return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'sales'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>{detailData.productName} 매출</h1>
                </div>
                <div className="admin-content-content">
                    <div className="content-period-detail-month">
                        <div className="form-group">
                            <label>총 매출 : </label>
                            <span>{numberComma(detailData.totalSales)}</span>
                        </div>
                        <div className="form-group">
                            <label>총 판매량 : </label>
                            <span>{numberComma(detailData.totalSalesQuantity)}</span>
                        </div>
                        <div className="form-group">
                            <label>{detailData.year}년 매출 : </label>
                            <span>{numberComma(detailData.yearSales)}</span>
                        </div>
                        <div className="form-group">
                            <label>{detailData.year}년 판매량 : </label>
                            <span>{numberComma(detailData.yearSalesQuantity)}</span>
                        </div>
                        <div className="form-group">
                            <label>전년 대비 매출 : </label>
                            <span>{numberComma(detailData.lastYearComparison)}</span>
                        </div>
                        <div className="form-group">
                            <label>{detailData.year - 1}년 매출 : </label>
                            <span>{numberComma(detailData.lastYearSales)}</span>
                        </div>
                        <div className="form-group">
                            <label>{detailData.year - 1}년 판매량 : </label>
                            <span>{numberComma(detailData.lastYearSalesQuantity)}</span>
                        </div>
                    </div>
                    <div className="content-product-detail-month mt-5-pe">
                        <div className="admin-product-sales-detail-header">
                            <h3>월별 매출</h3>
                        </div>
                        <table className="admin-content-table mt-5-pe">
                            <thead>
                                <tr>
                                    <th>월</th>
                                    <th>매출</th>
                                    <th>판매량</th>
                                    <th>주문량</th>
                                </tr>
                            </thead>
                            <tbody>
                            {monthSales.map((monthData, index) => {
                                return (
                                    <tr key={index}>
                                        <td>{monthData.date}</td>
                                        <td>{numberComma(monthData.sales)}</td>
                                        <td>{numberComma(monthData.salesQuantity)}</td>
                                        <td>{numberComma(monthData.orderQuantity)}</td>
                                    </tr>
                                )
                            })}
                            </tbody>
                        </table>
                    </div>
                    <div className="content-product-detail-option">
                        <ProductOptionSales
                            headerText={'총'}
                            salesData={optionTotalSales}
                        />
                        <ProductOptionSales
                            headerText={`${detailData.year}년`}
                            salesData={optionYearSales}
                        />
                        <ProductOptionSales
                            headerText={`${detailData.year - 1}년`}
                            salesData={optionLastYearSales}
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}

function ProductOptionSales(props) {
    const { headerText, salesData} = props;

    return(
        <div className="content-product-detail-option-content mt-5-pe">
            <div className="product-detail-option-content-header mt-5-pe">
                <h3>옵션별 {headerText} 매출 정보</h3>
            </div>
            <div className="content-product-detail-option-content-content">
                {salesData.map((option, index) => {
					const optionText = getProductOption({
						size: option.size,
						color: option.color,
					});
                    
                    const text = `${optionText} 매출 : ${numberComma(option.optionSales)}, 판매량 : ${numberComma(option.optionSalesQuantity)}`

                    return (
                        <div key={index} className="product-sales-detail-option b1-s-lg mt-5-p">
                            <span>{text}</span>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}
export default AdminProductSalesDetail;