exports.unwrap_properties = (jsons) => {
	let ar = (Array.isArray(jsons)) ? jsons : [jsons];

	for (let json of ar) {
		for (let property in json) {
			if (Array.isArray(json[property]) && json[property].length == 1) {
				json[property] = json[property][0];
			}
		}
	}

	return jsons;
};

exports.delete_properties = (jsons, properties) => {
	let ar_jsons = (Array.isArray(jsons)) ? jsons : [jsons];
	let ar_properties = (Array.isArray(properties)) ? properties : [properties];

	for (let json of ar_jsons) {
		for (let property of ar_properties) {
			delete json[property];
		}
	}

	return jsons;
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