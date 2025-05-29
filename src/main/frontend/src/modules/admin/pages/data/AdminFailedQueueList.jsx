import React, {useState, useEffect} from 'react';

import { getFailedQueueList, retryDLQMessages } from '../../services/adminDataService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';

import AdminSideNav from '../../components/AdminSideNav';
import DefaultButton from '../../../../common/components/DefaultButton';

/*
    RabbitMQ DLQ 메시지 조회

    메시지 자체 조회가 아닌 DLQ에 있는 메시지 개수를 조회.
    모든 DLQ에 메시지가 존재하지 않는다면 empty List가 오게 됨.

    버튼을 통해 메시지가 존재하는 모든 DLQ 재시도 처리.
*/
function AdminFailedQueueList() {
	const [data, setData] = useState([]);

	useEffect(() => {
		const getList = async () => {
			try {
				const res = await getFailedQueueList();

				setData(res.data);
			} catch (error) {
				console.log(error);
			}
		}

		getList();
	}, []);

	//재시도 버튼 이벤트
	const handleRetryBtn = async () => {
		try {
			const res = await retryDLQMessages(data);

			if(res.data.message === RESPONSE_MESSAGE.OK) 
				alert('실패한 메시지를 재시도합니다.\n메시지량에 따라 처리 시간이 상이할 수 있습니다.');
		} catch (error) {
			console.log(error);
		}
	}
	
	return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'data'}
            />
            <div className="admin-content">
                <div className="admin-content-header admin-product-header">
                    <h1>처리 실패 메시지</h1>
                    <FailedQueueBtn
                        data={data}
                        handleRetryBtn={handleRetryBtn}
                    />
                </div>
                <div className="admin-content-content">
                    <FailedListData
                        data={data}
                    />
                </div>
            </div>
        </div>
    )
}

function FailedQueueBtn(props) {
    const { data, handleRetryBtn } = props;

    if(data.length === 0)
        return null;
    
	return (
		<DefaultButton
			btnText={'재시도'}
			onClick={handleRetryBtn}
		/>
	)
}

function FailedListData(props) {
    const { data } = props;

    if(data.length === 0) {
        return (
            <span>미처리된 실패 메시지가 존재하지 않습니다.</span>
        )
    }else {
        return (
            <table className="admin-content-table">
                <thead>
                    <tr>
                        <th>QueueName</th>
                        <th>메시지 개수</th>
                    </tr>
                </thead>
                <tbody>
                {data.map((data, index) => {
                    return (
                        <tr key={index} className="admin-order-body-tr">
                            <td>{data.queueName}</td>
                            <td>{data.messageCount}</td>
                        </tr>
                    )
                })}
                </tbody>
            </table>
        )
    }
}

export default AdminFailedQueueList;