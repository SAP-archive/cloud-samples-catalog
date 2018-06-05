var fs = require("fs");
var childproc = require("child_process");

if (fs.existsSync("../package.json")) { // true at build-time, false at CF staging time
	var npmInstallCmd = "npm install";
	if (process.env.npm_config__sap_registry) {
		npmInstallCmd = "npm config set registry https://registry.npmjs.org/ && npm config set @sap:registry " + process.env.npm_config__sap_registry + " && npm install";
	}
	childproc.execSync(npmInstallCmd + "  && npm run build", { cwd: "..", shell: true, stdio: "inherit" });
}
