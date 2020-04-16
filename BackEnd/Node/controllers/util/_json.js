exports.unwrap_properties = (json) => {
	for (let property in json) {
		if (Array.isArray(json[property]) && json[property].length == 1) {
			json[property] = json[property][0];
		}
	}
	return json;
};

exports.get_value = (json, default_value, ...properties) => {
	// json must be a JSON
	let i, v;

	// handle exception: no JSON
	if (!json) return default_value;

	v = json;
	for (i = 0; i < properties.length; i++) {
		v = v[properties[i]];
		if (!v) return default_value;
	}
	return v;
};