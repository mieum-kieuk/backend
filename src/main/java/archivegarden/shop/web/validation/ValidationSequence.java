package archivegarden.shop.web.validation;

import archivegarden.shop.web.validation.ValidationGroups.NotBlankGroup;
import archivegarden.shop.web.validation.ValidationGroups.PatternGroup;
import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({Default.class, NotBlankGroup.class, PatternGroup.class})
public interface ValidationSequence {
}
