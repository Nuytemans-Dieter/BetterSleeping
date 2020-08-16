# Contributing guidelines
Thank you for being willing to contribute.
Please try to follow these guidelines when contributing to this project. This way, the project remains well organised.

## Creating issues
- Try to fill in the chosen issue template as good as possible if they match your issue/suggestion. Otherwise, feel free to create a blank issue.
- Filling in all fields may be a bit of work for a bug report so if you're not sure if it actually is a bug, you can submit a bug inquiry to avoid wasted work.

## Submitting a pull request
- Try to follow the general style of the code and document new methods with javadoc comments.
- Please make sure your pull request resolves an issue and that the issue is not labeled as `suggestion`. This is an issue whose faith is still to be decided.
- If there is no existing issue for your PR, please make an issue before submitting your PR.
- Please do not add new configuration options to the config files unless absolutely necessary. We try to keep config clutter to an absolute minimum.
- When making an addition to a language file, please make sure to reflect that change in all files. Eg. Fixing a typo in a comment (fix it in every file, or in none) or adding a new message (add it in every language, or don't add it). This is to prevent confusion for server owners and when tracking down related issues in the future.
- Adding a new language is very welcome! Just make sure that you used the most recent language file to do your translations and that you add the new language to the readme.md in the language folder.
- Please do not make any of the mistakes described in the [beginner programming mistakes](https://www.spigotmc.org/threads/beginner-programming-mistakes-and-why-youre-making-them.278876/) topic on the Spigot forums.
- You should also understand that build breaking pull requests will not be authored until they are fixed.
