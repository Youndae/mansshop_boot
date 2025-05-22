export function toggleReplyInputStatus(replyData, index, setReplyData, setModifyTextValue) {
	const currentData = replyData[index];
	const newData = {
		...currentData,
		inputStatus: !currentData.inputStatus,
	};

	const newReplyData = [...replyData];
	newReplyData[index] = newData;

	setReplyData(newReplyData);
	if(!currentData.inputStatus)
		setModifyTextValue(currentData.replyContent);
}