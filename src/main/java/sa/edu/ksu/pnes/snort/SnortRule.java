package sa.edu.ksu.pnes.snort;

public class SnortRule {

	private String id;
	private String desc;
	private String code;
	
	public SnortRule(String rule) {
		this.parseRule(rule);
	}
	private void parseRule(String rule) {
		this.code = rule;
		this.setRuleId(rule);
		this.setRuleDesc(rule);
		
	}
	private void setRuleId(String rule) {
		var idIndex = rule.indexOf("sid:") + "sid:".length();
		StringBuffer sb  = new StringBuffer();
		if(idIndex!= -1) {
			for(int i = idIndex;i < rule.length();i++)
			{
				if(rule.charAt(i) == ';')
					break;
				sb.append(rule.charAt(i));
				
			}
		}
		this.id = sb.toString();
	}
	private void setRuleDesc(String rule) {
                //msg:"'    |
		var idIndex = rule.indexOf("msg:\"\'") + "msg:\"\'".length();
		StringBuffer sb  = new StringBuffer();
		if(idIndex!= -1) {
			for(int i = idIndex;i < rule.length();i++)
			{
				if(rule.charAt(i) == '|')
					break;
				sb.append(rule.charAt(i));
				
			}
		}
		this.desc = sb.toString();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
}
