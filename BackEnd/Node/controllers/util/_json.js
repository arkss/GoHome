exports.unwrap_properties = (json) => {
	for (let property in json) {
		if (Array.isArray(json[property]) && json[property].length == 1) {
			json[property] = json[property][0];
		}
	}
	return json;
};