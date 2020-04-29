let latencies = [];
req = (url, method = 'GET', data = {}) => {
	// Default options are marked with *
	let t_start, t_end;
	let requestInit = {
		method: method, // *GET, POST, PUT, DELETE, etc.
		mode: 'cors', // no-cors, cors, *same-origin
		cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
		credentials: 'same-origin', // include, *same-origin, omit
		headers: {
			'Content-Type': 'application/json',
			// 'Content-Type': 'application/x-www-form-urlencoded',
		},
		redirect: 'follow', // manual, *follow, error
		referrer: 'no-referrer', // no-referrer, *client
	};

	if (method == 'GET')
		requestInit.qs = data;
	else
		// body data type must match "Content-Type" header
		requestInit.body = JSON.stringify(data);

	t_start = Date.now();
	return fetch(url, requestInit)
	.then(response => {
		// parses JSON response into native JavaScript objects
		t_end = Date.now();
		return response.json();
	}, err => {
		t_end = Date.now();
		console.log(err);
		return {};
	})
	.then(json => {
		// save latency
		latencies.unshift(t_end - t_start);
		latencies.splice(7);

		// calculate recent avg. latency
		let sum = 0;
		for (let n of latencies)
			sum += n;
		console.log(`Avg. latency of recent calls: ${Math.round(sum / latencies.length)}ms`);

		return json;
	});
};